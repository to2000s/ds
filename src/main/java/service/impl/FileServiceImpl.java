package service.impl;

import model.Block;
import model.BlockInfo;
import model.FileTree;
import model.FileTreeNode;
import service.FileService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class FileServiceImpl extends UnicastRemoteObject implements FileService {

    public FileServiceImpl() throws RemoteException {
        super();
    }

    public TreeMap<String, FileTreeNode> getFilesByName(String name) throws RemoteException {
        TreeMap<String, FileTreeNode> fileTreeNodes = new TreeMap<String, FileTreeNode>();

        Map<String, FileTreeNode> nameFileMap = FileTree.getFileTree().getNameFileMap();
        if (nameFileMap.get(name) == null) {
            return null;
        }
        long id = nameFileMap.get(name).getId();

        List<FileTreeNode> fileList = FileTree.getFileTree().getFileList();
        for (FileTreeNode node : fileList) {
            if (node.getPid() == id) {
                fileTreeNodes.put(node.getName(), node);
            }
        }

        return fileTreeNodes;
    }

    public TreeMap<Integer, Block> getBlocksByName(String name) throws RemoteException {
        TreeMap<Integer, Block> blockTreeMap = new TreeMap<Integer, Block>();

        if (FileTree.getFileTree().getNameFileMap().get(name) == null) {
            return null;
        }
        long fid = FileTree.getFileTree().getNameFileMap().get(name).getId();

        for (Block node : BlockInfo.getBlockInfo().getBlocks()) {
            if (node.getFid() == fid && node.getBakNo() == 0) {
                blockTreeMap.put(node.getSort(), node);
            }
        }

        return blockTreeMap;
    }

    public boolean createDir(String path) {
        try {
            // 获取父文件夹名
            int idx = path.lastIndexOf("/");
            // 注意 /data 这种
            String parentPath;
            if (idx == 0) {
                parentPath = "/";
            } else {
                parentPath = path.substring(0, idx);
            }

            long pid = FileTree.getFileTree().getNameFileMap().get(parentPath).getId();
            long id = Collections.max(FileTree.getFileTree().getIdFileMap().keySet()) + 1;

            FileTreeNode fileTreeNode = new FileTreeNode(path, id, pid, 1, 0, System.currentTimeMillis());
            FileTree.getFileTree().getFileList().add(fileTreeNode);
            FileTree.getFileTree().getIdFileMap().put(id, fileTreeNode);
            FileTree.getFileTree().getNameFileMap().put(path, fileTreeNode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long createFile(String path, String fileName, long size) throws RemoteException {
        long pid = FileTree.getFileTree().getNameFileMap().get(path).getId();
        long id = Collections.max(FileTree.getFileTree().getIdFileMap().keySet()) + 1;

        String[] strings = fileName.split("/");
        String filePath;
        if ("/".equals(path)) {
            filePath = path + strings[strings.length - 1];
        } else {
            filePath = path + "/" + strings[strings.length - 1];
        }

        FileTreeNode fileTreeNode = new FileTreeNode(filePath, id, pid, 0, size, System.currentTimeMillis());
        FileTree.getFileTree().getFileList().add(fileTreeNode);
        FileTree.getFileTree().getIdFileMap().put(id, fileTreeNode);
        FileTree.getFileTree().getNameFileMap().put(filePath, fileTreeNode);

        return id;
    }

    public boolean createBlocks(List<Block> blocks) throws RemoteException {
        BlockInfo.getBlockInfo().getBlocks().addAll(blocks);
        return true;
    }

    public boolean delete(String path) throws RemoteException {
        if (FileTree.getFileTree().getNameFileMap().get(path) == null) {
            return false;
        }
        int type = FileTree.getFileTree().getNameFileMap().get(path).getType();

        if (type == 1) {
            return deleteDir(path);
        } else {
            return deleteFile(path);
        }
    }

    public boolean deleteDir(String path) throws RemoteException {
        try {
            TreeMap<String, FileTreeNode> fileTreeNodes = getFilesByName(path);
            if (fileTreeNodes == null) {
                return false;
            }
            if (fileTreeNodes.size() != 0) {
                for (String key : fileTreeNodes.keySet()) {
                    deleteFile(key);
                }
            }
            deleteFile(path);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFile(String path) throws RemoteException {

        try {

            if (FileTree.getFileTree().getNameFileMap().get(path) == null) {
                return false;
            }
            long fid = FileTree.getFileTree().getNameFileMap().get(path).getId();
            FileTree.getFileTree().getIdFileMap().remove(fid);
            FileTree.getFileTree().getNameFileMap().remove(path);

            //TODO 如果出问题可能造成数据不一致 map list

            // 删除目录元数据
            int idx;
            for (idx = 0; idx < FileTree.getFileTree().getFileList().size(); idx++) {
                if (FileTree.getFileTree().getFileList().get(idx).getName().equals(path)) {
                    break;
                }
            }
            FileTree.getFileTree().getFileList().remove(idx);


            // 删除Block元数据
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < BlockInfo.getBlockInfo().getBlocks().size(); i++) {
                if (BlockInfo.getBlockInfo().getBlocks().get(i).getFid() == fid) {
                    list.add(i);
                }
            }
            for (Integer item : list) {
                BlockInfo.getBlockInfo().getBlocks().remove(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updateFileSize(long id, long size) throws RemoteException {
        try {

            if (FileTree.getFileTree().getIdFileMap().get(id) == null) {
                return false;
            }
            String path = FileTree.getFileTree().getIdFileMap().get(id).getName();
            FileTree.getFileTree().getIdFileMap().get(id).setSize(size);
            FileTree.getFileTree().getNameFileMap().get(path).setSize(size);

            int idx;
            for (idx = 0; idx < FileTree.getFileTree().getFileList().size(); idx++) {
                if (FileTree.getFileTree().getFileList().get(idx).getName().equals(path)) {
                    break;
                }
            }
            FileTree.getFileTree().getFileList().get(idx).setSize(size);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
