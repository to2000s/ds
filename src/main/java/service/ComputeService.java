package service;

import model.Block;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.TreeMap;

/**
 * ComputeService类，提供分布式计算的相关服务接口
 */
public interface ComputeService extends Remote {

    /**
     * master向slave派发程序，并接收返回值
     */
    List<Block> dispatchTask(String jarPath, String MainTask, TreeMap<Integer, Block> blocks, long fid) throws RemoteException;

    /**
     * client向master发送程序，得到计算结果文件的Block信息
     */
    List<Block> giveTask(String jarPath, String MainTask, String input, String output) throws RemoteException;

    /**
     * slave执行程序
     */
    Block doTask(String jarPath, String MainTask, Block block, long fid) throws RemoteException;

    /**
     * 向指定主机传输程序jar包
     */
    Boolean deliverJar(String jarPath, String ip) throws RemoteException;

}
