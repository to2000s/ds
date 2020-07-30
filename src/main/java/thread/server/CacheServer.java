package thread.server;

import service.impl.CacheServiceImpl;
import util.ConfigUtil;
import util.RMIUtil;

import java.rmi.RemoteException;

/**
 * 注册CacheService
 */
public class CacheServer extends Thread {

    @Override
    public void run() {
        try {
            RMIUtil.bind(ConfigUtil.getCache_port(), "CacheService", new CacheServiceImpl());
            System.out.println("CacheService" + "\t" + ConfigUtil.getLocal_ip() + ":" + ConfigUtil.getCache_port());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
