import thread.server.CacheServer;
import thread.server.ComputeServer;
import thread.server.HostServer;

public class Slave {
    public static void main(String[] args) {

        HostServer hostServer = new HostServer();
        hostServer.start();

        CacheServer cacheServer = new CacheServer();
        cacheServer.start();

        ComputeServer computeSerer = new ComputeServer();
        computeSerer.start();

        System.out.println("Slave 已启动！");
    }
}
