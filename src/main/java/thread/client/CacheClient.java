package thread.client;

import service.CacheService;
import util.ConfigUtil;
import util.RMIUtil;

import java.rmi.RemoteException;

/**
 * 客户端使用的Cache接口
 */
public class CacheClient {

    private CacheService cacheService = null;

    public boolean put(String key, Object object) throws RemoteException {
        cacheService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getCache_port(), "CacheService");
        cacheService.put(key, object);
        cacheService.syncPut(key, object);
        return true;
    }

    public Object get(String key) throws RemoteException {
        cacheService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getCache_port(), "CacheService");
        return cacheService.get(key);
    }

    public Object get(String key, String ip) throws RemoteException {
        cacheService = RMIUtil.lookup(ip, ConfigUtil.getCache_port(), "CacheService");
        return cacheService.get(key);
    }

    public boolean remove(String key) throws RemoteException {
        cacheService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getCache_port(), "CacheService");
        cacheService.syncRemove(key);
        return true;
    }

    public boolean clear() throws RemoteException {
        cacheService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getCache_port(), "CacheService");
        cacheService.syncClear();
        return true;
    }

}
