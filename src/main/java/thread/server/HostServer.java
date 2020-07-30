package thread.server;

import service.impl.HostServiceImpl;
import util.ConfigUtil;
import util.RMIUtil;

import java.rmi.RemoteException;

/**
 * 注册HostService
 */
public class HostServer extends Thread {

    @Override
    public void run() {
        try {
            RMIUtil.bind(ConfigUtil.getHeartbeat_port(), "HostService", new HostServiceImpl());
            System.out.println("HostService" + "\t" + ConfigUtil.getLocal_ip() + ":" + ConfigUtil.getHeartbeat_port());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
