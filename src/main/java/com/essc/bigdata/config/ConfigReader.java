package com.essc.bigdata.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    protected static final Properties prop = new Properties();

    static {
        InputStream in1 = null;
        try {
            // 读取resources资源文件下配置文件
            in1 = ConfigReader.class.getClassLoader().getResourceAsStream("flume_monitor.properties");
            // 设置utf-8编码，确保中文不乱码
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in1, "utf-8"));
            prop.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
            try {
                if (in1 != null) {
                    in1.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private static String getProperty(String key) {

        if (prop.containsKey(key)) {
            return prop.getProperty(key);
        }
        return "";
    }

    public static boolean haveKey(String key) {

        return prop.containsKey(key);
    }

    public static String getString(String key) {

        return getProperty(key);
    }

    public static Integer getInteger(String key) {

        String value = getProperty(key);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public static Long getLong(String key) {

        String value = getProperty(key);
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0L;
    }

    public static Double getDouble(String key) {

        String value = getProperty(key);
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0d;
    }

    public static Boolean getBoolean(String key) {

        String value = getProperty(key);
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(getString("messageSubType"));
        System.out.println(getString("flumeMonitor"));
    }
}
