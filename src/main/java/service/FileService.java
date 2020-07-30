package service;

import model.Block;
import model.FileTreeNode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.TreeMap;

/**
 * server：文件服务 查、增、删  上传、下载？
 */
public interface FileService extends Remote {

    /**
     * 根据文件夹路径名获取子文件及子文件夹
     */
    TreeMap<String, FileTreeNode> getFilesByName(String name) throws RemoteException;

    /**
     * 根据文件路径名获取Block信息
     */
    TreeMap<Integer, Block> getBlocksByName(String name) throws RemoteException;

    /**
     * 创建新文件夹
     */
    boolean createDir(String path) throws RemoteException;

    /**
     * 上传创建新文件
     */
    long createFile(String path, String fileName, long size) throws RemoteException;

    /**
     * 添加 block 数据
     */
    boolean createBlocks(List<Block> blocks) throws RemoteException;

    /**
     * 删除，通过判断path是文件or文件夹调用合适的方法
     */
    boolean delete(String path) throws RemoteException;

    /**
     * 删除文件夹及子文件
     */
    boolean deleteDir(String path) throws RemoteException;

    /**
     * 删除文件（及Blocks）
     */
    boolean deleteFile(String path) throws RemoteException;

    /**
     * 修改文件大小
     */
    boolean updateFileSize(long id, long size) throws RemoteException;

}
