package model;

import java.io.Serializable;

/**
 * 文件块类
 * code： 文件块编码
 * fid： 所属文件ID
 * host： 所在主机IP
 * sort： 文件块顺序
 * bakNo： 备份数，从0开始
 */
public class Block implements Serializable {

    private String code;
    private long fid;
    private String host;
    private long size;
    private int sort;
    private int bakNo;

    public Block(String code, long fid, String host, long size, int sort, int bakNo) {
        this.code = code;
        this.fid = fid;
        this.host = host;
        this.size = size;
        this.sort = sort;
        this.bakNo = bakNo;
    }

    public String getCode() {
        return code;
    }

    public long getFid() {
        return fid;
    }

    public String getHost() {
        return host;
    }

    public long getSize() {
        return size;
    }

    public int getSort() {
        return sort;
    }

    public int getBakNo() {
        return bakNo;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setBakNo(int bakNo) {
        this.bakNo = bakNo;
    }
}
