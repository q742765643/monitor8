package com.piesat.common.filter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piesat.common.annotation.DecryptRequest;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.vo.HttpReq;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/15 9:30
 */
public class MyFormHttpMessageConverter implements HttpMessageConverter<Object> {

    private final List<MediaType> mediaTypes;
    private final ObjectMapper objectMapper;

    public MyFormHttpMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.mediaTypes = new ArrayList<>(1);
        this.mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return mediaTypes.contains(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return  mediaTypes.contains(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return mediaTypes;
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws
            IOException, HttpMessageNotReadableException {
        if (null!=clazz) {
            String content = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
            return content;
        } else {
            MediaType contentType = inputMessage.getHeaders().getContentType();
            Charset charset = (contentType != null && contentType.getCharset() != null ?
                    contentType.getCharset() : Charset.forName("UTF-8"));
            String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

            String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
            MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    result.add(name, value);
                }
            }
            return result;
        }
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        Class<?> clazz = o.getClass();
        if (true) {

        } else {
            String out = objectMapper.writeValueAsString(o);
            StreamUtils.copy(out.getBytes(Charset.forName("UTF-8")), outputMessage.getBody());
        }
    }

}
