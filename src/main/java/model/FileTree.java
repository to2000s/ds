package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * master读取的目录树元数据
 * 单例FileTree
 */
public class FileTree implements Serializable {

    private List<FileTreeNode> fileList;
    // 分别以id和name为主键的map，加快查询
    private Map<Long, FileTreeNode> idFileMap;
    private Map<String, FileTreeNode> nameFileMap;

    private FileTree() {}

    private static FileTree fileTree = new FileTree();

    public static FileTree getFileTree() {
        return fileTree;
    }

    public List<FileTreeNode> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileTreeNode> fileList) {
        this.fileList = fileList;
    }

    public Map<Long, FileTreeNode> getIdFileMap() {
        return idFileMap;
    }

    public void setIdFileMap(Map<Long, FileTreeNode> idFileMap) {
        this.idFileMap = idFileMap;
    }

    public Map<String, FileTreeNode> getNameFileMap() {
        return nameFileMap;
    }

    public void setNameFileMap(Map<String, FileTreeNode> nameFileMap) {
        this.nameFileMap = nameFileMap;
    }

}
