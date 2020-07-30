package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * CacheService类，提供对缓存服务端的远程操作接口
 */
public interface CacheService extends Remote {

    /**
     * 写入一条key-value缓存
     */
    boolean put(String key, Object object) throws RemoteException;

    /**
     * 通过key获取缓存
     */
    Object get(String key) throws RemoteException;

    /**
     * 通过key删除缓存
     */
    boolean remove(String key) throws RemoteException;

    /**
     * 清空缓存
     */
    boolean clear() throws RemoteException;

    /**
     * 同步写入，限master调用
     */
    boolean syncPut(String key, Object object) throws RemoteException;

    /**
     * 同步删除，限master调用
     */
    boolean syncRemove(String key) throws RemoteException;

    /**
     * 同步清空，限master调用
     */
    boolean syncClear() throws RemoteException;
}
