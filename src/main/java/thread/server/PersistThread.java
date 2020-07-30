package thread.server;

import model.BlockInfo;
import model.FileTree;
import util.ConfigUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 持久化线程，运行在master上，持久化元数据信息
 */
public class PersistThread extends Thread {

    @Override
    public void run() {
        new Timer("beatTimer").schedule(new TimerTask() {
            @Override
            public void run() {
                persist();
            }
        }, 10000,1000* 60);
    }

    private void persist() {
        String metadataPath = ConfigUtil.getFile_path();
        String blockinfoPath = ConfigUtil.getBlock_path();

        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(metadataPath));
            for (int i = 0; i < FileTree.getFileTree().getFileList().size(); i++) {
                String line = FileTree.getFileTree().getFileList().get(i).getName() +
                        "," +
                        FileTree.getFileTree().getFileList().get(i).getId() +
                        "," +
                        FileTree.getFileTree().getFileList().get(i).getPid() +
                        "," +
                        FileTree.getFileTree().getFileList().get(i).getType() +
                        "," +
                        FileTree.getFileTree().getFileList().get(i).getSize() +
                        "," +
                        FileTree.getFileTree().getFileList().get(i).getCreateTime();

                fileWriter.write(line);
                fileWriter.newLine();
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("元数据持久化失败！");
            e.printStackTrace();
        }

        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(blockinfoPath));
            for (int i = 0; i < BlockInfo.getBlockInfo().getBlocks().size(); i++) {
                String line = BlockInfo.getBlockInfo().getBlocks().get(i).getCode() +
                        "," +
                        BlockInfo.getBlockInfo().getBlocks().get(i).getFid() +
                        "," +
                        BlockInfo.getBlockInfo().getBlocks().get(i).getHost() +
                        "," +
                        BlockInfo.getBlockInfo().getBlocks().get(i).getSize() +
                        "," +
                        BlockInfo.getBlockInfo().getBlocks().get(i).getSort() +
                        "," +
                        BlockInfo.getBlockInfo().getBlocks().get(i).getBakNo();

                fileWriter.write(line);
                fileWriter.newLine();
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Block数据持久化失败！");
            e.printStackTrace();
        }

    }

}
