package thread.server;

import service.impl.ComputeServiceImpl;
import util.ConfigUtil;
import util.RMIUtil;

import java.rmi.RemoteException;

/**
 * 注册ComputeService
 */
public class ComputeServer extends Thread {

    @Override
    public void run() {
        try {
            RMIUtil.bind(ConfigUtil.getCompute_port(), "ComputeService", new ComputeServiceImpl());
            System.out.println("ComputeService" + "\t" + ConfigUtil.getLocal_ip() + ":" + ConfigUtil.getCompute_port());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
