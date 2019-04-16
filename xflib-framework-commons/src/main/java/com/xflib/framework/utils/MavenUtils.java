/**Copyright: Copyright (c) 2016, koradji*/
package com.xflib.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 找出maven下载出错的文件 Created by koradji on 20190415.
 */
public class MavenUtils {

    public static void main(String[] args) {
        showAllFiles("C:/Users/Administrator/.m2");
        System.out.println("the end");
    }

    /**
     * 根据目录dir,递归所有的文件按handle方法处理
     * 
     * @param dir
     */
    public static final void showAllFiles(String dir) {
        showAllFiles(new File(dir));
    }

    public static final void showAllFiles(File dir) {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                showAllFiles(fs[i]);
            }
            MavenUtil1.handle(fs[i]);
        }
    }

    private static class MavenUtil1 {
        
        private static MessageDigest messageDigest = null;
        private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        private static final int BUFFER_SIZE = 4096;

        static {
            try {
                messageDigest = MessageDigest.getInstance("SHA1");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        /**
         * 凡是以.sha1的文件结尾的文件,先将文件名去掉.sha1找到文件sourcefile,获取sourcefile文件的真实的sha1和从这个.sha1文件获取sha1,
         * 进行比照,如果不匹配,输出实际的sha1和期望的sha1
         * 
         * @param file
         */
        public static final void handle(File file) {
            String filename = file.getName();
            if (filename.endsWith(".sha1")) {
                String sourcename = filename.substring(0, filename.lastIndexOf('.'));
                File sourcefile = new File(file.getParent(), sourcename);
                byte[] sourcedata = getFileData(sourcefile);
                String sha1Real = digestData(sourcedata);
                String content = getFileString(file, "UTF-8");
                String sha1Check = content.split(" ")[0].trim();
                if (!sha1Real.equalsIgnoreCase(sha1Check)) {
                    System.out.println("rd /q /s " + sourcefile.getParent());
                }
            }
        }

        /**
         * 以16进制字符串形式返回数据的sha1
         * 
         * @param data
         *            data
         * @return str
         */
        private static String digestData(byte[] data) {
            messageDigest.update(data);
            data = messageDigest.digest();
            int len = data.length;
            StringBuilder buf = new StringBuilder(len * 2);
            for (int j = 0; j < len; j++) {
                buf.append(HEX_DIGITS[(data[j] >> 4) & 0x0f]).append(HEX_DIGITS[data[j] & 0x0f]);
            }
            return buf.toString();
        }

        /**
         * 根据文件与编码以String形式返回文件的数据
         * 
         * @param file
         *            文件名
         * @param charset
         *            字符集
         * @return str
         */
        private static String getFileString(File file, String charset) {
            InputStreamReader reader = null;
            StringBuilder out = null;
            try {
                reader = new InputStreamReader(new FileInputStream(file), charset);
                out = new StringBuilder();
                char[] buffer = new char[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    out.append(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return out.toString();
        }

        /**
         * 根据文件以byte[]形式返回文件的数据
         * 
         * @param file
         * @return
         */
        private static byte[] getFileData(File file) {
            FileInputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new ByteArrayOutputStream(BUFFER_SIZE);
                @SuppressWarnings("unused")
                int byteCount = 0;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    byteCount += bytesRead;
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return out.toByteArray();
        }
    }
}