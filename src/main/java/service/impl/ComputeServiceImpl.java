package service.impl;

import model.Block;
import service.ComputeService;
import service.FileService;
import service.HostService;
import util.ConfigUtil;
import util.JarUtil;
import util.RMIUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ComputeServiceImpl extends UnicastRemoteObject implements ComputeService {

    public ComputeServiceImpl() throws RemoteException{
        super();
    }

    public List<Block> dispatchTask(String jarPath, String mainTask, TreeMap<Integer, Block> blocks, long fid) throws RemoteException {

        final List<Block> results = new ArrayList<Block>();

        // 调用master的ComputeService，向slave传送jar包
        ComputeService computeService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getCompute_port(), "ComputeService");

        Set<String> ips = new HashSet<String>();
        for (Map.Entry<Integer, Block> block : blocks.entrySet()) {
            if (!ips.contains(block.getValue().getHost())) {
                ips.add(block.getValue().getHost());
            }
        }

        for (String ip : ips) {
            computeService.deliverJar(jarPath, ip);
        }

        for (Map.Entry<Integer, Block> item : blocks.entrySet()) {

            final Block block = item.getValue();
            final String path = jarPath;
            final String task = mainTask;
            final long id = fid;

            new Thread() {
                @Override
                public void run() {
                    ComputeService slaveComputeService = RMIUtil.lookup(block.getHost(), ConfigUtil.getCompute_port(), "ComputeService");
                    Block result = null;
                    try {
                        result = slaveComputeService.doTask(path, task, block, id);
//                        System.out.println("doTask " + block.getHost() + " " + result);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    results.add(result);
//                    System.out.println("results: " + block.getHost() + " " + results.get(0));
//                    System.out.println("results size: " + block.getHost() + " " + results.size());
                }
            }.start();
        }

        // 等待所有slave上的计算完成，获得result输出
        while (results.size() != blocks.size()) {
            // System.out.println(results.size() + " vs " + blocks.size());
            // 没有这个打印会卡住，为何？
            System.out.print("");
        }

        //

        return results;
    }

    public List<Block> giveTask(String jarPath, String mainTask, String input, String output) throws RemoteException {

        deliverJar(jarPath, ConfigUtil.getMaster_ip());
        String jarName = jarPath.split("/")[jarPath.split("/").length - 1];

        FileService fileService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getInfo_port(), "FileService");
        TreeMap<Integer, Block> blocks = fileService.getBlocksByName(input);
        //TODO 这里暂时没办法设置输出文件的大小，需要在 block 中添加信息
        long fid = fileService.createFile(output.substring(0, output.lastIndexOf("/")), output.split("/")[output.split("/").length - 1], -1);

        List<Block> list = dispatchTask(ConfigUtil.getCompute_path() + "/" + jarName, mainTask, blocks, fid);

        long size = 0;
        for (Block block : list) {
            size = size + block.getSize();
        }
        fileService.updateFileSize(fid, size);

        return list;
    }

    public Block doTask(String jarPath, String mainTask, Block block, long fid) {

        File file = new File(jarPath);
        if (file.isDirectory() && file.exists()) {
            file.delete();
        }

        File jarFile = new File(jarPath);

        String result_code = null;
        try {
            JarUtil.unjar(jarFile, new File(ConfigUtil.getCompute_path()));

            // 将jar包解压出的class文件添加到classPath
            ArrayList<URL> classPath = new ArrayList<URL>();
            classPath.add(new File( ConfigUtil.getCompute_path() + "/").toURL());
            classPath.add(jarFile.toURL());
            ClassLoader loader = new URLClassLoader(classPath.toArray(new URL[0]));
            Thread.currentThread().setContextClassLoader(loader);

            // 利用反射取得class和method
            Class<?> mainClass = Class.forName(mainTask, true, loader);
            Object object = mainClass.newInstance();
            Method main = mainClass.getMethod("execute", String.class);
            // 运行方法 获得返回文件块code
            result_code = (String) main.invoke(object, ConfigUtil.getStore_path() + "/" + block.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new Block(result_code,
                fid,
                block.getHost(),
                new File(ConfigUtil.getStore_path() + "/" + result_code).length(),
                block.getSort(),
                block.getBakNo());
    }

    /**
     * 向指定IP的主机发送jar包
     * jarPath 是发送端的路径
     */
    public Boolean deliverJar(String jarPath, String ip) {
        try {

            HostService hostService = RMIUtil.lookup(ip, ConfigUtil.getHeartbeat_port(), "HostService");
            // 如果已存在该jar包，删除并更新
            hostService.deleteExistFile(jarPath);
            // 上传jar包
            int port = hostService.getRandomPort();
            hostService.createReceiveFileServer(port, ConfigUtil.getCompute_path());

            Socket socket = new Socket(ip, port);
            InputStream inputStream = new FileInputStream(jarPath);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(jarPath.split("/")[jarPath.split("/").length - 1]);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, len);
            }

            inputStream.close();
            dataOutputStream.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
