package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * MD5校验工具类
 */
public class MD5Util {

    /**
     * 获取文件的md5值
     */
    public static String md5HashCode(String filePath) throws FileNotFoundException{
        FileInputStream inputStream = new FileInputStream(filePath);
        return md5HashCode(inputStream);
    }


    public static String md5HashCode(InputStream inputStream) {
        try {
            // MD5转换器
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            inputStream.close();
            // 转换为byte数组
            byte[] md5Bytes  = md.digest();
            // 转换为16进制
            BigInteger bigInt = new BigInteger(1, md5Bytes);
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
