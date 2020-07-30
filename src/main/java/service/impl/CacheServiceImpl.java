package service.impl;

import model.Cache;
import service.CacheService;
import util.ConfigUtil;
import util.RMIUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CacheServiceImpl extends UnicastRemoteObject implements CacheService {

    private CacheService cacheService;

    public CacheServiceImpl() throws RemoteException {
        super();
    }

    public boolean put(String key, Object object) throws RemoteException {
        try {
            Cache.getCache().put(key, object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key) throws RemoteException {
        return Cache.getCache().get(key);
    }

    public boolean remove(String key) throws RemoteException {
        try {
            Cache.getCache().remove(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clear() throws RemoteException {
        try {
            Cache.getCache().clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean syncPut(String key, Object object) throws RemoteException {

        for (int i = 0; i < ConfigUtil.getSlaves().length; i++) {
            cacheService = RMIUtil.lookup(ConfigUtil.getSlaves()[i], ConfigUtil.getCache_port(), "CacheService");
            if (cacheService == null) {
                return false;
            }
            cacheService.put(key, object);
        }
        return true;
    }

    public boolean syncRemove(String key) throws RemoteException {
        for (int i = 0; i < ConfigUtil.getSlaves().length; i++) {
            cacheService = RMIUtil.lookup(ConfigUtil.getSlaves()[i], ConfigUtil.getCache_port(), "CacheService");
            if (cacheService == null) {
                return false;
            }
            cacheService.remove(key);
        }
        return true;
    }

    public boolean syncClear() throws RemoteException {
        for (int i = 0; i < ConfigUtil.getSlaves().length; i++) {
            cacheService = RMIUtil.lookup(ConfigUtil.getSlaves()[i], ConfigUtil.getCache_port(), "CacheService");
            if (cacheService == null) {
                return false;
            }
            cacheService.clear();
        }
        return true;
    }
}
