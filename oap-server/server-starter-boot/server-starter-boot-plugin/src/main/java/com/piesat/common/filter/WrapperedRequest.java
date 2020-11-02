package com.piesat.common.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 18:00
 */
public class WrapperedRequest extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;
    private final Map<String, String[]> parameterMap; // 所有参数的Map集合
    private String requestBody = null;
    private HttpServletRequest req = null;

    public WrapperedRequest(HttpServletRequest request) {
        super(request);
        this.req = request;
        parameterMap = new HashMap(request.getParameterMap());
        this.customHeaders = new HashMap<>();
    }

    public WrapperedRequest(HttpServletRequest request, String requestBody) {
        super(request);
        parameterMap = new HashMap(request.getParameterMap());
        this.requestBody = requestBody;
        this.req = request;
        this.customHeaders = new HashMap<>();

    }

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    public void putParameterMap(Map<String, String[]> map) {
        this.parameterMap.putAll(map);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<String>(parameterMap.keySet());
        return vector.elements();
    }

    @Override
    public String getParameter(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return null;
        } else {
            return results[0];
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return null;
        }

        return results;
    }

    @Override
    public String getHeader(String name) {
        // check the custom headers first
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // create a set of the custom header names
        Set<String> set = new HashSet<>(customHeaders.keySet());

        // now add the headers from the wrapped request object
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            // add the names of the request headers into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }


    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletRequestWrapper#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new StringReader(requestBody));
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            private InputStream in = new ByteArrayInputStream(
                    requestBody.getBytes(req.getCharacterEncoding()));

            @Override
            public int read() throws IOException {
                return in.read();
            }

            @Override
            public boolean isFinished() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isReady() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // TODO Auto-generated method stub

            }
        };
    }
}
