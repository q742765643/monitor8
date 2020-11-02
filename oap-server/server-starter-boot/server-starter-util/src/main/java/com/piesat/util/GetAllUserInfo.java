package com.piesat.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAllUserInfo {
    public String getAllUserInfo() {
        String strURL = ConfigUtil.getProperty("urls", "cmadaas") + "api/rest/user/getAllUserInfo?key=sodCF5881E4AA6D4235D16";
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(strURL);
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置属性
            connection.setRequestMethod("GET");
            // 建立链接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;// 循环读取
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {// 关闭流
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return buffer.toString();
    }

    public String getUserInfo(String user_id) {
        String strURL = ConfigUtil.getProperty("urls", "cmadaas") + "api/rest/user/getUserInfo/" + user_id + "?key=sodCF5881E4AA6D4235D16";
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(strURL);
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置属性
            connection.setRequestMethod("GET");
            // 建立链接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;// 循环读取
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {// 关闭流
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return buffer.toString();
    }
}
