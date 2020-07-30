package thread.server;

import model.Block;
import model.BlockInfo;
import model.FileTree;
import model.FileTreeNode;
import service.impl.FileServiceImpl;
import util.ConfigUtil;
import util.RMIUtil;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * 读取元数据，注册FileService
 */
public class FileServer extends Thread {

    @Override
    public void run() {
        readMetadata();
        try {
            RMIUtil.bind(ConfigUtil.getInfo_port(), "FileService", new FileServiceImpl());
            System.out.println("FileService" + "\t" + ConfigUtil.getLocal_ip() + ":" + ConfigUtil.getInfo_port());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void readMetadata() {
        // 读取文件metadata，加载到内存
        List<FileTreeNode> fileList = getFileList(ConfigUtil.getFile_path());
        FileTree.getFileTree().setFileList(fileList);
        FileTree.getFileTree().setIdFileMap(getIdFileMap(fileList));
        FileTree.getFileTree().setNameFileMap(getNameFileMap(fileList));
        // 读取块metadata，加载到内存
        List<Block> blockList = getBlockList(ConfigUtil.getBlock_path());
        BlockInfo.getBlockInfo().setBlocks(blockList);

    }

    private List<FileTreeNode> getFileList(String path) {

        File file = new File(path);
        if (!file.exists()) {
            try {
                // 元数据文件不存在，就创建一个根路径
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                String line = "/,1,0,1,0," + System.currentTimeMillis();
                writer.write(line);
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<FileTreeNode> fileList = new ArrayList<FileTreeNode>();

        // 读文件
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] str = line.split(",");
                FileTreeNode fileTreeNode = new FileTreeNode(str[0],
                        Long.valueOf(str[1]),
                        Long.valueOf(str[2]),
                        Integer.valueOf(str[3]),
                        Long.valueOf(str[4]),
                        Long.valueOf(str[5]));
                fileList.add(fileTreeNode);
            }
            fileReader.close();
        } catch (IOException e) {
            System.out.println("读取元数据文件失败！");
            e.printStackTrace();
        }

        return fileList;
    }

    /**
     * 建立以Id为键的file map，加快查询
     */
    private Map<Long, FileTreeNode> getIdFileMap(List<FileTreeNode> fileList) {
        Map<Long, FileTreeNode> idFileMap = new HashMap<Long, FileTreeNode>();
        for (int i = 0; i < fileList.size(); i++) {
            idFileMap.put(fileList.get(i).getId(), fileList.get(i));
        }
        return idFileMap;
    }

    /**
     * 建立以Name为键的file map，加快查询
     */
    private Map<String, FileTreeNode> getNameFileMap(List<FileTreeNode> fileList) {
        Map<String, FileTreeNode> nameFileMap = new HashMap<String, FileTreeNode>();
        for (int i = 0; i < fileList.size(); i++) {
            nameFileMap.put(fileList.get(i).getName(), fileList.get(i));
        }
        return nameFileMap;
    }

    /**
     * 读取Block数据文件，创建单例BlockInfo
     */
    private List<Block> getBlockList(String path) {
        List<Block> blockList = new ArrayList<Block>();

        File file = new File(path);
        if (!file.exists()) {
            try {
                // 元数据文件不存在，就创建一个
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                String[] str = line.split(",");
                Block block = new Block(str[0],
                        Long.valueOf(str[1]),
                        str[2],
                        Long.valueOf(str[3]),
                        Integer.valueOf(str[4]),
                        Integer.valueOf(str[5]));
                blockList.add(block);
            }
            fileReader.close();
        } catch (IOException e) {
            System.out.println("读取Block数据文件失败！");
            e.printStackTrace();
        }
        return blockList;
    }

}
