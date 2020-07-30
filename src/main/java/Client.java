import model.Block;
import model.FileTreeNode;
import service.FileService;
import thread.client.CacheClient;
import thread.client.ComputeClient;
import thread.client.DownloadFileThread;
import thread.client.UploadFileThread;
import util.ConfigUtil;
import util.RMIUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    public static void main(String[] args) throws RemoteException, InterruptedException {

        FileService fileService = RMIUtil.lookup(ConfigUtil.getMaster_ip(), ConfigUtil.getInfo_port(), "FileService");
        TreeMap<String, FileTreeNode> files;
        TreeMap<Integer, Block> blocks;
        List<Block> blockList;

        CacheClient cacheClient = new CacheClient();

        System.out.println("HIDS 1.0");
        System.out.print(">");

        Scanner scanner = new Scanner(System.in);
        String input;
        while (!"exit".equals(input = scanner.nextLine())) {
            if ("help".equals(input)) {
                System.out.println("file：分布式文件处理");
                System.out.println("cache：分布式缓存");
                System.out.println("cal：分布式计算");
                System.out.println("help：帮助");
                System.out.println("exit：退出");
            } else if ("file".equals(input)) {

                System.out.print("<file>");

                while (!"exit".equals(input = scanner.nextLine())) {
                    String[] arg = input.split(" ");

                    // 各类命令
                    if ("list".equals(arg[0])) {
                        if (arg.length == 1) {
                            files = fileService.getFilesByName("/");
                        } else {
                            files = fileService.getFilesByName(arg[1]);
                        }

                        if (files == null) {
                            System.out.println("不存在该文件夹！");
                        } else {
                            

                            // 打印
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            System.out.println("名称" +
                                    "\t" +
                                    "大小" +
                                    "\t" +
                                    "修改日期" +
                                    "\t" + "\t" +
                                    "类型");
                            for (Map.Entry<String, FileTreeNode> entry : files.entrySet()) {
                                System.out.println(entry.getKey().split("/")[entry.getKey().split("/").length - 1] +
                                        "\t" +
                                        entry.getValue().getSize() +
                                        "\t" +
                                        simpleDateFormat.format(new Date(entry.getValue().getCreateTime())) +
                                        "\t" +
                                        (entry.getValue().getType() == 1 ? "文件夹" : "文件"));
                            }
                        }
                    } else if ("detail".equals(arg[0])) {
                        if (arg.length == 1) {
                            System.out.println("请输入文件路径！");
                        } else {
                            blocks = fileService.getBlocksByName(arg[1]);

                            if (blocks == null) {
                                System.out.println("不存在该文件！");
                            } else {

                                // 打印
                                for (Map.Entry<Integer, Block> node : blocks.entrySet()) {
                                    System.out.println(
                                            node.getKey() +
                                                    "\t" +
                                                    node.getValue().getCode() +
                                                    "\t" +
                                                    node.getValue().getHost() +
                                                    "\t" +
                                                    node.getValue().getSize());
                                }
                            }
                        }
                    } else if ("mkdir".equals(arg[0])) {
                        if (fileService.createDir(arg[1])) {
                            System.out.println("文件夹创建成功！");
                        } else {
                            System.out.println("文件夹创建失败！");
                        }
                    } else if ("rm".equals(arg[0])) {
                        if (fileService.delete(arg[1])) {
                            System.out.println("删除成功！");
                        } else {
                            System.out.println("删除失败！");
                        }
                    } else if ("upload".equals(arg[0])) {
                        if (arg.length < 3) {
                            System.out.println("格式错误，请重新输入！");
                        } else {
                            File file = new File(arg[2]);
                            if (!file.exists()) {
                                System.out.println("该文件不存在！");
                            } else if (file.isDirectory()) {
                                System.out.println("路径名非文件！");
                            } else {

                                long fid = fileService.createFile(arg[1], arg[2], file.length());

                                UploadFileThread uploadFileThread = new UploadFileThread();
                                uploadFileThread.setFilePath(arg[2]);
                                uploadFileThread.setFid(fid);
                                uploadFileThread.start();

                                uploadFileThread.join();
                                blockList = uploadFileThread.getBlocks();
                                fileService.createBlocks(blockList);
                            }
                        }
                    } else if ("download".equals(arg[0])) {
                        if (arg.length == 1) {
                            System.out.println("格式错误，请重新输入！");
                        } else {
                            String localDir;
                            if (arg.length == 2) {
                                localDir = "./";
                            } else {
                                localDir = arg[2];
                            }

                            blocks = fileService.getBlocksByName(arg[1]);
                            String localPath = localDir + File.separator + arg[1].split("/")[arg[1].split("/").length - 1];

                            DownloadFileThread downloadFileThread = new DownloadFileThread();
                            downloadFileThread.setBlocks(blocks);
                            downloadFileThread.setLocalPath(localPath);
                            downloadFileThread.start();
                            downloadFileThread.join();
                        }
                    } else if ("help".equals(arg[0])) {
                        System.out.println("list <HIDS文件夹路径>");
                        System.out.println("\t" + "列出文件夹下所有文件");
                        System.out.println("block <HIDS文件路径>");
                        System.out.println("\t" + "列出文件块信息");
                        System.out.println("mkdir <HIDS文件夹路径>");
                        System.out.println("\t" + "创建新文件夹，确保上一级文件夹存在");
                        System.out.println("rm <HIDS文件/文件夹路径>");
                        System.out.println("\t" + "删除文件或文件夹");
                        System.out.println("upload <HIDS文件夹路径> <本地文件路径>");
                        System.out.println("\t" + "上传本地文件");
                        System.out.println("download <HIDS文件路径> <本地文件夹路径>");
                        System.out.println("\t" + "下载文件到本地");

                        System.out.print("<file>");
                        continue;
                    } else if ("exit".equals(arg[0])) {
                        break;
                    } else {
                        System.out.println("格式错误，请重新输入！");
                        System.out.print("<file>");
                        continue;
                    }
                    System.out.print("<file>");
                }
            } else if ("cache".equals(input)) {
                System.out.print("<cache>");

                while (!"exit".equals(input = scanner.nextLine())) {
                    String[] arg = input.split(" ");

                    // 各类命令
                    if ("put".equals(arg[0])) {
                        if (arg.length != 3) {
                            System.out.println("格式错误：put <key> <value>");
                        } else {
                            cacheClient.put(arg[1], arg[2]);
                        }
                    } else if ("get".equals(arg[0])) {
                        if (arg.length == 1) {
                            System.out.println("格式错误：get <key> | get <key> <ip>");
                        } else if (arg.length == 2) {
//                            while (cacheService == null) {
//                                Random random = new Random();
//                                String ip = ConfigUtil.getSlaves()[random.nextInt(ConfigUtil.getSlaves().length - 1)];
//                                cacheService = RMIUtil.lookup(ip, ConfigUtil.getCache_port(), "CacheService");
//                                System.out.println(cacheService.get(arg[1]).toString());
//                            }
                            Random random = new Random();
                            String ip = ConfigUtil.getSlaves()[random.nextInt(ConfigUtil.getSlaves().length - 1)];
                            System.out.println((String) cacheClient.get(arg[1], ip));
                        } else if (arg.length == 3) {
                            System.out.println((String) cacheClient.get(arg[1], arg[2]));
                        }
                    } else if ("delete".equals(arg[0])) {
                        if (arg.length != 2) {
                            System.out.println("格式错误：delete <key>");
                        } else {
                            cacheClient.remove(arg[1]);
                        }
                    } else if ("clear".equals(arg[0])) {
                        cacheClient.clear();
                    } else if ("exit".equals(arg[0])) {
                        break;
                    } else if ("help".equals(arg[0])) {
                        System.out.println("put <key> <value>");
                        System.out.println("\t" + "添加key-value缓存");
                        System.out.println("get <key>");
                        System.out.println("\t" + "获取key对应的value");
                        System.out.println("get <key> <ip>");
                        System.out.println("\t" + "从指定节点获取key对应的value");
                        System.out.println("delete <key>");
                        System.out.println("\t" + "删除指定key的value");
                        System.out.println("clear");
                        System.out.println("\t" + "清空缓存");
                        System.out.println("exit");
                        System.out.println("\t" + "返回");

                        System.out.print("<cache>");
                        continue;
                    } else {
                        System.out.println("不支持该命令，请查询help");
                        System.out.print("<cache>");
                        continue;
                    }

                    System.out.print("<cache>");
                }
            } else if ("compute".equals(input)) {
                System.out.print("<compute>");
                while (!"exit".equals(input = scanner.nextLine())) {
                    String[] arg = input.split(" ");

                    if ("help".equals(arg[0])) {
                        System.out.println("exec <jar包路径> <主程序> <源数据HIDS路径> <结果输出HIDS路径>");
                        System.out.print("<compute>");
                        continue;
                    } else if ("exec".equals(arg[0])) {
                        if (arg.length < 5) {
                            System.out.println("格式错误：exec <jar包路径> <主程序> <源数据HIDS路径> <结果输出HIDS路径>");
                        } else {
                            ComputeClient computeClient = new ComputeClient();
                            fileService.createBlocks(computeClient.execute(arg[1], arg[2], arg[3], arg[4]));
                        }
                    } else {
                        System.out.println("不支持该命令，请查询help");
                        System.out.print("<compute>");
                        continue;
                    }
                    System.out.print("<compute>");

                }
            } else {
                System.out.println("不支持该命令，请查询help");
            }

            System.out.print(">");

        }

    }

}
