package model;

import java.util.HashMap;

/**
 * 缓存，继承HashMap
 * 每个缓存服务端节点保持一个缓存类实例
 */
public class Cache extends HashMap<String, Object> {

    private Cache() {}

    private static Cache cache = new Cache();
    public static Cache getCache() {
        return cache;
    }
}
