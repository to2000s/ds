package thread.client;

import model.Block;
import service.HostService;
import util.ConfigUtil;
import util.MD5Util;
import util.RMIUtil;

import java.io.*;
import java.net.Socket;
import java.util.TreeMap;

public class DownloadFileThread extends Thread {

    private TreeMap<Integer, Block> blocks;
    private String localPath;

    @Override
    public void run() {
        try {
            downloadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile() throws IOException {
        // 指定的slave 下载文件的端口
        int blockNum = blocks.size();
        String code = blocks.get(0).getCode().split("_")[0];
        byte[] buffer = new byte[1024 * 1024];

        File file = new File(localPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        HostService hostService = null;
        for (int i = 0; i < blockNum; i++) {
            // blocks是有序的
            hostService = RMIUtil.lookup(blocks.get(i).getHost(), ConfigUtil.getHeartbeat_port(), "HostService");
            int port = hostService.getRandomPort();
            hostService.createSendFileServer(port, ConfigUtil.getStore_path());

            Socket socket = new Socket(blocks.get(i).getHost(), port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(blocks.get(i).getCode());
            socket.shutdownOutput();

            InputStream inputStream = socket.getInputStream();
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                fileOutputStream.flush();
            }
            socket.shutdownInput();

            inputStream.close();
            dataOutputStream.close();
            outputStream.close();
            socket.close();
        }

        fileOutputStream.close();
        if (!blocks.get(0).getCode().startsWith("result_")) {
            if (!code.equals(MD5Util.md5HashCode(localPath))) {
                boolean result = file.delete();
                System.out.println("下载文件MD5校验值有误，可能传输出错！");
                System.out.println(result ? "删除成功！" : "删除失败,请手动删除！");
            }
        }
    }

    public TreeMap<Integer, Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(TreeMap<Integer, Block> blocks) {
        this.blocks = blocks;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
