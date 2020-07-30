package model;

import java.io.Serializable;

/**
 * 文件系统的一条元数据
 * name：全路径名
 * id： 节点id
 * pid： 父节点id
 * type： 类型，1是目录，0是文件
 * size： 大小，单位是字节
 * createTime: 创建时间，时间戳
 */
public class FileTreeNode implements Serializable {

    private String name;
    private long id;
    private long pid;
    private int type;
    private long size;
    private long createTime;

    public FileTreeNode(String name, long id, long pid, int type, long size, long createTime) {
        this.name = name;
        this.id = id;
        this.pid = pid;
        this.type = type;
        this.size = size;
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getPid() {
        return pid;
    }

    public int getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
