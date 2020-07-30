package model;

import java.io.Serializable;
import java.util.List;

/**
 * master读取的文件块元数据
 * 单例
 */
public class BlockInfo implements Serializable {

    private List<Block> blocks;

    private BlockInfo() {

    }

    private static final BlockInfo blockInfo = new BlockInfo();

    public static BlockInfo getBlockInfo() {
        return blockInfo;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
