package com.piesat.common.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.vo.HttpReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/14 11:40
 */
@RequiredArgsConstructor
public class CustomEncryptHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final ObjectMapper objectMapper;
    @Override
    public boolean canRead(Class<?> clazz,MediaType mediaType) {
        return true;
    }
    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        HttpReq httpReq= objectMapper.readValue(StreamUtils.copyToByteArray(inputMessage.getBody()), HttpReq.class);

        return objectMapper.readValue(AESUtil.aesDecrypt(httpReq.getData()), javaType);
    }
    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
        return super.readInternal(clazz, inputMessage);
    }
    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException {
         super.writeInternal(object, type, outputMessage);
    }





}
