package thread.client;

import model.Block;
import service.HostService;
import util.ConfigUtil;
import util.MD5Util;
import util.RMIUtil;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UploadFileThread extends Thread {

    private String filePath;
    private long fid;
    // blocks 返回的消息
    private List<Block> blocks = new ArrayList<Block>();

    @Override
    public void run() {
        try {
            uploadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFile() throws IOException {

        // 切割文件
        String fileHash = MD5Util.md5HashCode(filePath);
        File file = new File(filePath);

        String[] hosts = ConfigUtil.getSlaves();
        int bakNum = ConfigUtil.getStore_replication();
        int maxSize = ConfigUtil.getStore_maxsize();
        int blockNum = (int)(file.length() / (maxSize * 1024 * 1024)) + 1;

        // 读文件流
        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024 * 1024];  // 1M的缓存  20,20,3

        for (int i = 0; i < blockNum; i++) {
            HostService hostService = null;
            int idx = i % hosts.length;
            while (hostService == null) {
                idx = (idx + 1) % hosts.length;
                hostService = RMIUtil.lookup(hosts[idx], ConfigUtil.getHeartbeat_port(), "HostService");
            }

            int port = hostService.getRandomPort();
            hostService.createReceiveFileServer(port, ConfigUtil.getStore_path());

            Socket socket = new Socket(hosts[idx], port);
            // 分块读取文件
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // 获取Block Code，即在slave的文件名，尽量不重复 hash + 时间
            String code = fileHash + "_" + System.currentTimeMillis();
            dataOutputStream.writeUTF(code);

            // 一次 maxSize
            long size = 0;
            for (int k = 0; k < maxSize; k++) {
                int len = inputStream.read(buffer);
                if (len != -1) {
                    dataOutputStream.write(buffer, 0, len);
                    size = size + len;
                }
            }

            //告诉服务器文件已传输完毕 成功后关闭
            socket.shutdownOutput();
            dataOutputStream.close();
            outputStream.close();
            socket.close();

            // 返回block信息 准备发往master
            Block block = new Block(code, fid, hosts[idx], size, i, 0);
            blocks.add(block);

            if (bakNum > 1) {
                List<Block> baks = hostService.makeReplication(code, fid, size, i, bakNum);
                blocks.addAll(baks);
            }

        }

        inputStream.close();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
