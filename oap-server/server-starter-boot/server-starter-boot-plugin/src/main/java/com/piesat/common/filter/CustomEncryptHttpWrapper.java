package com.piesat.common.filter;

import org.springframework.context.annotation.Configuration;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/14 11:29
 */
public class CustomEncryptHttpWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headers = new HashMap<>(8);
    private Map<String, String[]> parameterMap=new HashMap<>(); // 所有参数的Map集合
    private final byte[] data;

    public CustomEncryptHttpWrapper(HttpServletRequest request, String content) {
        super(request);
        data = content.getBytes(Charset.forName("UTF-8"));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headers.put(key, request.getHeader(key));
        }
        
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(Collections.singletonList(headers.get(name)));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return  Collections.enumeration(headers.keySet());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return !isReady();
            }

            @Override
            public boolean isReady() {
                return inputStream.available() > 0;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return super.getReader();
    }
    /**
     * 获取所有参数名
     *
     * @return 返回所有参数名
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<String>(parameterMap.keySet());
        return vector.elements();
    }

    /**
     * 获取指定参数名的值，如果有重复的参数名，则返回第一个的值 接收一般变量 ，如text类型
     *
     * @param name
     *            指定参数名
     * @return 指定参数名的值
     */
    @Override
    public String getParameter(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0){
            return null;
        }
        else {
            return results[0];
        }
    }

    /**
     * 获取指定参数名的所有值的数组，如：checkbox的所有数据
     * 接收数组变量 ，如checkobx类型
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0){
            return null;
        }
        else {
            int length = results.length;
            for (int i = 0; i < length; i++) {
            }
            return results;
        }
    }
}