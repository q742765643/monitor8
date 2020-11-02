package com.piesat.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.piesat.common.annotation.DecryptRequest;
import com.piesat.common.vo.HttpReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/29 10:46
 */
@Slf4j
public class DecryptUtil {
    public static void decrypt(HttpServletRequest request, HttpServletResponse response, Object o) {
        boolean shouldDecrypt = true;
        /*****===========1.判断token判断是否需要解密=================*****/
       /* String token = request.getHeader("authorization");
        if(StringUtils.isNotNull(token)&&token.equals("88888888")){
            shouldDecrypt=false;
        }*/
        /*****===========2.判断注解判断是否需要解密=================*****/
        if (o instanceof ResourceHttpRequestHandler) {
            return;
        }
        HandlerMethod h = (HandlerMethod) o;
        DecryptRequest decryptRequest = h.getMethod().getAnnotation(DecryptRequest.class);
        if (decryptRequest != null && decryptRequest.value() == false) {
            shouldDecrypt = false;
        }
        /*****===========3.判断是否有参数需要解密=================*****/
        Map<String, String[]> param = new HashMap<>(8);
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            param.put(name, request.getParameterValues(name));
        }
        if (param.size() == 0) {
            shouldDecrypt = false;
        }
        if (null == param.get("data")) {
            shouldDecrypt = false;
        }
        /*****===========4.进行解密类参数是否为空=================*****/
        String requestBody = "";
        if (shouldDecrypt) {
            requestBody = param.get("data")[0];
            if (StringUtils.isNull(requestBody) || "".equals(requestBody)) {
                shouldDecrypt = false;
                request.setAttribute("REQUEST_RESOLVER_PARAM_MAP_NAME", null);
                return;
            }
        }
        /*****===========5.进行解密=================*****/
        if (shouldDecrypt) {
            String data = AESUtil.aesDecrypt(requestBody).replace(" ", "");
            log.info(request.getRequestURI());
            Map<String, Object> map = JSON.parseObject(data, Map.class);
            Map<String, String[]> parameterMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object oo = entry.getValue();

                if (oo instanceof JSONArray) {
                    String parmFit = "";
                    JSONArray jsonArray = (JSONArray) oo;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        parmFit += String.valueOf(jsonArray.get(i)) + "&&&";
                    }
                    parameterMap.put(entry.getKey(), parmFit.split("&&&"));
                } else if (null == oo) {
                    parameterMap.put(entry.getKey(), null);
                } else {
                    String parmFit = String.valueOf(oo);
                    parameterMap.put(entry.getKey(), parmFit.split("&&&"));
                }
            }

            request.setAttribute("REQUEST_RESOLVER_PARAM_MAP_NAME", parameterMap);
        } else {
            /*****===========5.不解密=================*****/
            request.setAttribute("REQUEST_RESOLVER_PARAM_MAP_NAME", param);

        }


    }

    public static boolean decrypt(HttpInputMessage inputMessage, MethodParameter parameter, Map<String, ByteArrayInputStream> map) {
        boolean shouldDecrypt = true;
        /*****===========1.判断token判断是否需要解密=================*****/
        /*List<String> tokens=inputMessage.getHeaders().get("authorization");
        if(null==tokens||tokens.size()==0){
            return shouldDecrypt;
        }
        String token=tokens.get(0);
        if(token.equals("88888888")){
            shouldDecrypt=false;
            return shouldDecrypt;
        }*/
        DecryptRequest decryptRequest = parameter.getMethod().getAnnotation(DecryptRequest.class);
        if (decryptRequest != null && decryptRequest.value() == false) {
            shouldDecrypt = false;
            return shouldDecrypt;
        }
        try {
            String data = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
            HttpReq httpReq = JSON.parseObject(data, HttpReq.class);
            ByteArrayInputStream inputStream = null;
            if (null == httpReq.getData() || "".equals(httpReq.getData())) {
                shouldDecrypt = false;
                inputStream = new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8")));
            } else {
                inputStream = new ByteArrayInputStream(AESUtil.aesDecrypt(httpReq.getData()).getBytes(Charset.forName("UTF-8")));
            }
            map.put("REQUEST_RESOLVER_PARAM_MAP_NAME", inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return shouldDecrypt;


    }
}
