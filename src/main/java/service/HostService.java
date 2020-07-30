package service;

import model.Block;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 获取某主机的状态信息、方法调用
 */
public interface HostService extends Remote{

    /**
     * 获取该主机上可用的端口
     */
    int getRandomPort() throws RemoteException;

    /**
     * 删除该主机上指定路径的文件
     */
    boolean deleteExistFile(String path) throws RemoteException;

    /**
     * 创建一个ServerSocket，接收文件
     */
    boolean createReceiveFileServer(int port, String path) throws RemoteException;

    /**
     * 创建一个ServerSocket，发送文件
     */
    boolean createSendFileServer(int port, String path) throws RemoteException;

    /**
     * 对该主机上的文件进行备份操作
     */
    List<Block> makeReplication(String code, long fid, long size, int sort, int bakNum) throws IOException;
}
