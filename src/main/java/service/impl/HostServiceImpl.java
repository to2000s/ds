package service.impl;

import model.Block;
import service.HostService;
import util.ConfigUtil;
import util.RMIUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class HostServiceImpl extends UnicastRemoteObject implements HostService{

    public HostServiceImpl() throws RemoteException {
        super();
    }

    public int getRandomPort() throws RemoteException {
        Random random = new Random();
        int port;

        while (true) {
            // 20000~40000的随机端口
            port = random.nextInt(20000) + 20000;
            try {
                // 能连接上说明该端口已被占用
                Socket socket = new Socket("127.0.0.1", port);
                socket.close();
            } catch (IOException e) {
                break;
            }
        }
        return port;
    }

    public boolean deleteExistFile(String path) throws RemoteException {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public boolean createReceiveFileServer(int port, String path) throws RemoteException {
        final int p = port;
        final String pa = path;

        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(p);

                    Socket socket = server.accept();
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    // 文件块code
                    String filename = dataInputStream.readUTF();
                    FileOutputStream fileOutputStream = new FileOutputStream(pa + File.separator + filename);

                    int len = 0;
                    byte[] buffer = new byte[1024 * 1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        fileOutputStream.flush();
                    }

                    fileOutputStream.close();
                    dataInputStream.close();
                    inputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return true;
    }

    public boolean createSendFileServer(int port, String path) throws RemoteException {
        final int p = port;
        final String pa = path;

        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(p);

                    Socket socket = server.accept();
                    InputStream inputStream = socket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    String code = dataInputStream.readUTF();
                    FileInputStream fileInputStream = new FileInputStream(pa + File.separator + code);

                    OutputStream outputStream = socket.getOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024 * 1024];
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    socket.shutdownOutput();

                    outputStream.close();
                    dataInputStream.close();
                    inputStream.close();
                    fileInputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return true;
    }

    public List<Block> makeReplication(String code, long fid, long size, int sort, int bakNum) throws IOException {

        List<Block> blocks = new ArrayList<Block>();
        Set<String> hosts = new HashSet<String>();
        hosts.add(ConfigUtil.getLocal_ip());

        // 最初的文件块bakNum为0
        int tryNum = 0;
        for (int i = 1; i < bakNum; i++) {
            HostService hostService = null;
            int idx = -1;
            while (hostService == null) {
                Random random = new Random();
                idx = random.nextInt(ConfigUtil.getSlaves().length);
                if (hosts.contains(ConfigUtil.getSlaves()[idx])) {
                    tryNum++;
                    continue;
                }
                hostService = RMIUtil.lookup(ConfigUtil.getSlaves()[idx], ConfigUtil.getHeartbeat_port(), "HostService");
                if (tryNum > 100) {
                    // 尝试此处过多，说明可以利用的节点数不足以支持备份，可以抛出异常提示
                    return null;
                }
                tryNum++;
            }
            hosts.add(ConfigUtil.getSlaves()[idx]);

            File file = new File(ConfigUtil.getStore_path() + File.separator + code);
            InputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024];

            int port = hostService.getRandomPort();
            hostService.createReceiveFileServer(port, ConfigUtil.getStore_path());

            Socket socket = new Socket(ConfigUtil.getSlaves()[idx], port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF(code);

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, len);
            }

            //告诉服务器文件已传输完毕 成功后关闭
            socket.shutdownOutput();
            dataOutputStream.close();
            outputStream.close();
            socket.close();

            // 返回block信息 准备发往master
            Block block = new Block(code, fid, ConfigUtil.getSlaves()[idx], size, sort, i);
            blocks.add(block);
        }
        return blocks;
    }
}
