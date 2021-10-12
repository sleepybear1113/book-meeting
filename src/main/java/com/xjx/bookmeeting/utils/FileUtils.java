package com.xjx.bookmeeting.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 19:43
 */
@Slf4j
public class FileUtils {
    public static <T> void writeToFile(T t, String path) {
        if (t == null) {
            return;
        }
        String s = JSON.toJSONString(t);
        writeToFile(s, path);
    }

    public static void writeToFile(String s, String path) {
        if (s == null || s.length() == 0) {
            return;
        }

        File file = new File(path);
        if (!file.getParentFile().exists() && !file.isDirectory()) {
            boolean mkdir = file.getParentFile().mkdir();
            if (!mkdir) {
                log.warn("创建文件夹失败 " + path);
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    public static String readFile(String path) {
        StringBuilder res = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line = bufferedReader.readLine();
            while (line != null) {
                res.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return res.toString();
    }

    public static <T> T readFile(String path, Class<T> clazz) {
        String s = readFile(path);
        if (StringUtils.isBlank(s)) {
            return null;
        }

        return JSON.parseObject(s, clazz);
    }

    public static List<String> listFilesInDir(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<String> res = new ArrayList<>();
        if (files == null) {
            return res;
        }
        for (File f : files) {
            res.add(f.getPath());
        }
        return res;
    }
}
