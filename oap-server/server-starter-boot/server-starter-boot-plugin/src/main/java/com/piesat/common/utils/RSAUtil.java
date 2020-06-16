package com.piesat.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-03-25 11:00
 **/
public class RSAUtil {
    public static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    static {
       keyMap.put(0,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChBaGiw4ugvRg+v5BtP2jrE0DOjmNH0LbATy5W0bzaKS+1i6HLiX8wSS9qKFbq3Bvl29grjYKwebGE5STdimgOR6yB6lfxYuHEWAOb7TkxaRXxoYGDPHYJfjFZS7B+BMyRGhnCX09rUChPeTq6EKRp2X5CNzvu7+pQ+kDPW3rsfwIDAQAB");
       keyMap.put(1,"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKEFoaLDi6C9GD6/kG0/aOsTQM6OY0fQtsBPLlbRvNopL7WLocuJfzBJL2ooVurcG+Xb2CuNgrB5sYTlJN2KaA5HrIHqV/Fi4cRYA5vtOTFpFfGhgYM8dgl+MVlLsH4EzJEaGcJfT2tQKE95OroQpGnZfkI3O+7v6lD6QM9beux/AgMBAAECgYBAZGAfFtPfk77+WN0I0zLBzxE7iPVq4qwye3esgHNlStMpZo1tN68FQD8V1MvtX9hIM4Je6Fg6+m9jFb+IWLEDYmNC0Io18YuP0o5NdAvCx5oDp9dK2jeyWeZ7A2gdtxywWzoCf4Ycd+AKR+aADBFtxLXcuPiIbiKdcF1s1KV4AQJBANMzMSXtMH4MLCSDa1L6Skc2AM+mMMHyeqj4RrQyFzSV4kXeyJqznGCDQhxALBXFnR5P3v5eJ4F0waAUpNm9498CQQDDLZ8Hzmbp0iSQ+rg82gfDgg7PF8eGDegOzpenF42UkPHMoJXOhuemOTE7XoBbmSfJhjUg5ySDm0TGD7+94wthAkEA0WES/JKlXJEcwiY3pE/Wi7qSG0qbU+vcht982PA/6TYe2T4Air497cCzLebAzeTX21E7tdoKOGFUBCvzMpr66QJAWkzUI/dxk8J+2ni1Hqo9J04X7eZxkGsErz0T2uHBxjedN3AgfzHZIQWa6n3ZYwej6c/m6rcmJKGEmyIUMxVFYQJBAIptU8obhT/94vEsKQAqAlhByHduD0oyu9Tpr7FKNl0Y6Kau8MpteGsaYbQDJv2UKcX2XzU5RiXfEFUrYpfHh6s=");

    }
    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        //genKeyPair();
        System.out.println(JSON.toJSONString(keyMap));
        //加密字符串
        String message = "111111";
        System.out.println("随机生成的公钥为:" + keyMap.get(0));
        System.out.println("随机生成的私钥为:" + keyMap.get(1));
        String messageEn = encrypt(message,keyMap.get(0));
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        messageEn="WfWaMsTfJu5y8rGbsYftxZeTqWZ+b5wbKla5jYbpEvBfP6pQzGcM/fCJVuZlOdzom4+fV+1dfiy6/XDKtgpk3/BFdrRKEYEgPG8S/u40xyJ9MVB7fJOF43t5PocmWzWlOmXLM/k9E2CiautsFF/7auXQjA23NRoDlPthD539lz4=";

        String messageDe = decrypt(messageEn,keyMap.get(1));
        System.out.println("还原后的字符串为:" + messageDe);
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            String outStr = new String(cipher.doFinal(inputByte));
            return outStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }


}

