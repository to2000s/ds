package util;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * RMI服务工具类
 */
public class RMIUtil {

    /**
     * 注册服务
     */
    public static <T extends Remote> void bind(int port, String service, T object) {
        try {
            System.setProperty("java.rmi.server.hostname", ConfigUtil.getLocal_ip());
            LocateRegistry.createRegistry(port);
//            "rmi://127.0.0.1:20191/HostService"
            Naming.rebind("rmi://127.0.0.1:" + port + "/" + service, object);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询注册表获取服务
     */
    public static <T extends Remote> T lookup(String ip, int port, String service) {
//        "rmi://10.23.147.176:20191/HostService"
        try {
            return (T)Naming.lookup("rmi://" + ip + ":" + port + "/" + service);
        } catch (NotBoundException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

}
