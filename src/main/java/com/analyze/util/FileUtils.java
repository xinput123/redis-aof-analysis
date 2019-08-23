package com.analyze.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:xinput.xx@gmail.com">xinput</a>
 * @Date: 2019-08-21 22:50
 */
public class FileUtils {

    private static final String REDIS_AOF = ".aof";

    public static String[] fileName(String[] params) throws UnsupportedEncodingException {
        String aofFile = "";
        String outFile = "redis.log";
        String[] files = new String[2];

        if (params.length == 0) {
            System.out.println("未接受到任何参数,默认寻找当前目录下的aof文件.");
            String currentPath = getCurrentPath();
            aofFile = FileUtils.getAofFileName(currentPath);
            outFile = currentPath + File.separator + outFile;

            files[0] = aofFile;
            files[1] = outFile;

            return files;
        }

        // 参数为1时，默认输入的aof文件名称
        if (params.length == 1) {
            aofFile = params[0];
        }

        files[0] = aofFile;
        files[1] = outFile;
        return files;
    }

    public static String getAofFileName(String path) {
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(REDIS_AOF)) {
                    return true;
                }
                return false;
            }
        });

        String redisAofFile = "";
        if (files.length > 0) {
            redisAofFile = files[0].getPath();
        }

        return redisAofFile;
    }

    public static String getCurrentPath() throws UnsupportedEncodingException {
        String path = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = java.net.URLDecoder.decode(path, "UTF-8");
        return path.substring(0, path.lastIndexOf(File.separator));
    }

    /**
     * 通过BufferedRandomAccessFile读取文件,推荐
     *
     * @param file     源文件
     * @param encoding 文件编码
     * @param pos      偏移量
     * @param num      读取量
     * @return pins文件内容，pos当前偏移量
     */
    public static Map<String, Object> BufferedRandomAccessFileReadLine(File file, String encoding, long pos, int num) {
        Map<String, Object> res = Maps.newHashMap();
        List<String> messages = Lists.newArrayList();
        res.put("messages", messages);
        BufferedRandomAccessFile reader = null;

        try {
            reader = new BufferedRandomAccessFile(file, "r");
            reader.seek(pos);

            StringBuilder sb;

            int commondNum = 0;
            while (commondNum < num) {
                // 内容为： *2,表示这个命令有几个参数组成
                String aa = reader.readLine();
                if (StringUtils.isEmpty(aa)) {
                    break;
                }

                // int n ,表示这个命令有 n 个参数组成
                int paramNum = Integer.parseInt(aa.substring(1));
                sb = new StringBuilder();
                for (int i = 0; i < paramNum; i++) {
                    String message = reader.readLine();
                    // 表示命令的长度
                    int messageNum = Integer.parseInt(message.substring(1));

                    String str = "";
                    for (int j = 0; j < messageNum; j++) {
                        str = str + (char) reader.readByte();
                    }
                    sb.append(" ").append(str);

//                    System.out.println("偏移量 : " + reader.getFilePointer());
                    // 每次读到文件的最后一行，会有一个换行符
                    reader.readLine();
//                    System.out.println("偏移量 : " + reader.getFilePointer());
                }

                messages.add(sb.toString());
                commondNum++;

            }

            res.put("pos", reader.getFilePointer());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return res;
    }

    public static void writeFile(List<String> messages, String file, String encode) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file, true), encode)) {
            // 在1500w里随机1000w数据
            messages.forEach(message -> {
                try {
                    out.write(message + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputFile = "/Users/yuanlai/Downloads/test.aof";
        String outputFile = "/Users/yuanlai/Downloads/testout.aof";
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);//10M缓存
            FileWriter fw = new FileWriter(outputFile);

            while (in.ready()) {
                String line = in.readLine();
                fw.append(line + " ");
            }
            in.close();
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
