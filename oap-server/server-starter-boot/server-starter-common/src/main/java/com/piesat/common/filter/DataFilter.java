package com.piesat.common.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piesat.common.vo.HttpReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 17:57
 */

@WebFilter(urlPatterns = {"/*"}, filterName = "DataFilter")
@Slf4j
@RequiredArgsConstructor
public class DataFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        T bean = clazz.newInstance();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        /*  String contentType = request.getContentType();
        String requestBody = null;
        boolean shouldEncrypt = false;
        if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE)) {
            shouldEncrypt = true;
            requestBody = convertInputStreamToString(request.getInputStream());
        }
        if (!shouldEncrypt) {
           filterChain.doFilter(request,response);
        }else{
            //WrapperedRequest wrapRequest = new WrapperedRequest( (HttpServletRequest) request, AESUtil.aesDecode(httpReq.getData()));
            //WrapperedResponse wrapResponse = new WrapperedResponse((HttpServletResponse) response);
            //CustomEncryptHttpWrapper wrapper = new CustomEncryptHttpWrapper(request, requestBody);
            //wrapper.putHeader("content-type", MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE);
            CustomEncryptHttpWrapper wrapRequest = new CustomEncryptHttpWrapper(request,requestBody);
            filterChain.doFilter(wrapRequest, response);
        }*/
        //} else {
        //HttpReq httpReq= objectMapper.readValue(requestBody, HttpReq.class);
        //CustomEncryptHttpWrapper wrapper = new CustomEncryptHttpWrapper(request, requestBody);
        //wrapper.putHeader("content-type", MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE);
        //filterChain.doFilter(wrapper, response);
        //WrapperedRequest wrapRequest = new WrapperedRequest( (HttpServletRequest) request, AESUtil.aesDecode(httpReq.getData()));
        //WrapperedResponse wrapResponse = new WrapperedResponse((HttpServletResponse) response);
            /*filterChain.doFilter(wrapRequest, wrapResponse);
            byte[] data = wrapResponse.getResponseData();
            String responseBodyMw=new String(data);
            writeResponse(response, responseBodyMw);*/






            /*if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
                HttpReq httpReq = getRequestBody(servletRequest);
                //解密请求报文
                String requestBodyMw = AESUtil.aesDecode(httpReq.getData());
                log.info("解密请求数据："+requestBodyMw);
                WrapperedRequest wrapRequest = new WrapperedRequest( (HttpServletRequest) servletRequest, requestBodyMw);
                WrapperedResponse wrapResponse = new WrapperedResponse((HttpServletResponse) servletResponse);
                filterChain.doFilter(wrapRequest, wrapResponse);
                byte[] data = wrapResponse.getResponseData();
                //log.info("原始返回数据： " + new String(data, "utf-8"));
                // 加密返回报文
                //String responseBodyMw = Base64.getEncoder().encodeToString(data);
                String responseBodyMw=new String(data);
                //log.info("加密返回数据： " + responseBodyMw);
                writeResponse(servletResponse, responseBodyMw);
            }else if (StringUtils.substringMatch(contentType, 0, MediaType.APPLICATION_JSON_VALUE)) {

                HttpReq httpReq = convertFormToString(servletRequest);
            }*/


    }

    private String convertFormToString(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(8);
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            result.put(name, request.getParameter(name));
        }
        try {
            HttpReq httpReq = mapToBean(result, HttpReq.class);
            return httpReq.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        return StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
    }

    private HttpReq getRequestBody(HttpServletRequest req) {
        try {
            BufferedReader reader = req.getReader();
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            HttpReq httpReq = JSON.parseObject(json, HttpReq.class);

            return httpReq;
        } catch (IOException e) {
            log.info("请求体读取失败" + e.getMessage());
        }
        return null;
    }

    private void writeResponse(ServletResponse response, String responseString)
            throws IOException {
        PrintWriter out = response.getWriter();
        out.print(responseString);
        out.flush();
        out.close();
    }
}

