package com.piesat.skywalking.util;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-18 11:21
 **/
public class HtObjectSerializer implements RedisSerializer {
    @Override
    public byte[] serialize(Object object) throws SerializationException {
        byte[] result = new byte[0];
        if (object == null) {
            return result;
        } else {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
            if (!(object instanceof Serializable)) {
                throw new SerializationException("requires a Serializable payload but received an object of type [" + object.getClass().getName() + "]");
            } else {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                    objectOutputStream.writeObject(object);
                    objectOutputStream.flush();
                    result = byteStream.toByteArray();
                    return result;
                } catch (IOException var5) {
                    throw new SerializationException("serialize error, object=" + object, var5);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        Object result = null;
        if (bytes != null && bytes.length != 0) {
            try {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                result = objectInputStream.readObject();
                return result;
            } catch (IOException var5) {
                throw new SerializationException("deserialize error", var5);
            } catch (ClassNotFoundException var6) {
                throw new SerializationException("deserialize error", var6);
            }
        } else {
            return result;
        }
    }
}

