package util;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar包操作工具类
 */
public class JarUtil {

    /**
     * 解压jar包文件
     */
    public static void unjar(File jar, File dir) throws IOException {
        JarFile jarFile = new JarFile(jar);

        try {
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    try {
                        File file = new File(dir, entry.getName());
                        if (!file.getParentFile().mkdirs()) {
                            if (!file.getParentFile().isDirectory()) {
                                throw new IOException("Failed to create " + file.getParentFile().toString());
                            }
                        }

                        OutputStream outputStream = new FileOutputStream(file);
                        try {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, len);
                            }
                        } finally {
                            outputStream.close();
                        }
                    } finally {
                        inputStream.close();
                    }
                }
            }
        } finally {
            jarFile.close();
        }
    }

}
