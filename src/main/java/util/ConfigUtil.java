package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取配置工具类
 */
public class ConfigUtil {

    static {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String local_ip;
    private static String master_ip;
    private static String[] slaves;

    private static String file_path;
    private static String block_path;
    private static String store_path;
    private static String compute_path;

    private static int info_port;
    private static int heartbeat_port;
    private static int cache_port;
    private static int compute_port;

    private static int store_maxsize;
    private static int store_replication;
    private static int heartbeat_delay;
    private static int heartbeat_period;


    public static void init() throws IOException {

        Properties properties = getProp();
        // 以下是IDE调试时使用的resource中的配置
//        Properties properties = getProp("/ds.properties");

        // 能不能自动生成？
        local_ip = properties.getProperty("ds.local.ip");
        master_ip = properties.getProperty("ds.master.ip");
        slaves = properties.getProperty("ds.slaves.ip").split(",");

        file_path = properties.getProperty("ds.file.path");
        block_path = properties.getProperty("ds.block.path");
        store_path = properties.getProperty("ds.store.path");
        compute_path = properties.getProperty("ds.compute.path");

        info_port = Integer.valueOf(properties.getProperty("ds.info.port"));
        heartbeat_port = Integer.valueOf(properties.getProperty("ds.heartbeat.port"));
        cache_port = Integer.valueOf(properties.getProperty("ds.cache.port"));
        compute_port = Integer.valueOf(properties.getProperty("ds.compute.port"));

        store_maxsize = Integer.valueOf(properties.getProperty("ds.store.maxsize"));
        store_replication = Integer.valueOf(properties.getProperty("ds.store.relication"));
        heartbeat_delay = Integer.valueOf(properties.getProperty("ds.heartbeat.delay"));
        heartbeat_period = Integer.valueOf(properties.getProperty("ds.heartbeat.period"));

    }
    
    private static Properties getProp(String path) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = ConfigUtil.class.getResourceAsStream(path);

        properties.load(inputStream);
        return properties;
    }

    // 打包成jar包后需要用下面的配置
    private static Properties getProp() throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = new FileInputStream(new File("ds.properties"));
        properties.load(inputStream);
        return properties;
    }

    public static String getLocal_ip() {
        return local_ip;
    }

    public static void setLocal_ip(String local_ip) {
        ConfigUtil.local_ip = local_ip;
    }

    public static String getMaster_ip() {
        return master_ip;
    }

    public static void setMaster_ip(String master_ip) {
        ConfigUtil.master_ip = master_ip;
    }

    public static String[] getSlaves() {
        return slaves;
    }

    public static void setSlaves(String[] slaves) {
        ConfigUtil.slaves = slaves;
    }

    public static String getFile_path() {
        return file_path;
    }

    public static void setFile_path(String file_path) {
        ConfigUtil.file_path = file_path;
    }

    public static String getBlock_path() {
        return block_path;
    }

    public static void setBlock_path(String block_path) {
        ConfigUtil.block_path = block_path;
    }

    public static String getStore_path() {
        return store_path;
    }

    public static void setStore_path(String store_path) {
        ConfigUtil.store_path = store_path;
    }

    public static String getCompute_path() {
        return compute_path;
    }

    public static void setCompute_path(String compute_path) {
        ConfigUtil.compute_path = compute_path;
    }

    public static int getInfo_port() {
        return info_port;
    }

    public static void setInfo_port(int info_port) {
        ConfigUtil.info_port = info_port;
    }

    public static int getHeartbeat_port() {
        return heartbeat_port;
    }

    public static void setHeartbeat_port(int heartbeat_port) {
        ConfigUtil.heartbeat_port = heartbeat_port;
    }

    public static int getCache_port() {
        return cache_port;
    }

    public static void setCache_port(int cache_port) {
        ConfigUtil.cache_port = cache_port;
    }

    public static int getCompute_port() {
        return compute_port;
    }

    public static void setCompute_port(int compute_port) {
        ConfigUtil.compute_port = compute_port;
    }

    public static int getStore_maxsize() {
        return store_maxsize;
    }

    public static void setStore_maxsize(int store_maxsize) {
        ConfigUtil.store_maxsize = store_maxsize;
    }

    public static int getStore_replication() {
        return store_replication;
    }

    public static void setStore_replication(int store_replication) {
        ConfigUtil.store_replication = store_replication;
    }

    public static int getHeartbeat_delay() {
        return heartbeat_delay;
    }

    public static void setHeartbeat_delay(int heartbeat_delay) {
        ConfigUtil.heartbeat_delay = heartbeat_delay;
    }

    public static int getHeartbeat_period() {
        return heartbeat_period;
    }

    public static void setHeartbeat_period(int heartbeat_period) {
        ConfigUtil.heartbeat_period = heartbeat_period;
    }
}
