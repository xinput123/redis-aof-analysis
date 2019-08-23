package com.analyze;

import com.analyze.util.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * main method
 */
public class Main {

    private static final String ENCODING = "UTF-8";
    private static final int NUM = 50;

    public static void main(String[] args) throws UnsupportedEncodingException {


        if (args.length != 2) {
            System.out.println("请输入aof文件名称和输出文件的名称...");
            System.exit(1);
        }

        String aofFile = args[0];
        String outFile = args[1];
        if (!aofFile.toLowerCase().endsWith(".aof")) {
            System.out.println("请输入以aof为结尾的文件名称...");
            System.exit(1);
        }

        String currentPath = FileUtils.getCurrentPath();
        aofFile = FileUtils.getAofFileName(FileUtils.getCurrentPath());
        outFile = currentPath + File.separator + outFile;

        File file = new File(aofFile);

        long start = System.currentTimeMillis();
        long pos = 0L;
        while (true) {
            Map<String, Object> res = FileUtils.BufferedRandomAccessFileReadLine(file, ENCODING, pos, NUM);
            // 如果返回结果为空结束循环
            if (MapUtils.isEmpty(res)) {
                break;
            }
            List<String> messages = (List<String>) res.get("messages");
            if (CollectionUtils.isNotEmpty(messages)) {
                FileUtils.writeFile(messages, outFile, ENCODING);

                if (messages.size() < NUM) {
                    break;
                }
            } else {
                break;
            }
            pos = (Long) res.get("pos");
        }

        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
    }

}
