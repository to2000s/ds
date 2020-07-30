package thread.client;

import model.Block;
import service.ComputeService;
import service.impl.ComputeServiceImpl;

import java.rmi.RemoteException;
import java.util.List;

/**
 * 客户端执行并发程序的接口
 */
public class ComputeClient {

    private ComputeService computeService = null;

    public List<Block> execute(String jarName, String mainTask, String input, String output) {
        try {
            computeService = new ComputeServiceImpl();
            return computeService.giveTask(jarName, mainTask, input, output);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}
