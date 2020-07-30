import thread.server.*;

public class Master {

    public static void main(String[] args) {

        HostServer hostServer = new HostServer();
        hostServer.start();

        FileServer fileServer = new FileServer();
        fileServer.start();
        // 持久化线程
        PersistThread persistThread = new PersistThread();
        persistThread.start();

        CacheServer cacheServer = new CacheServer();
        cacheServer.start();

        ComputeServer computeSerer = new ComputeServer();
        computeSerer.start();

        System.out.println("Master 已启动！");
    }

}
