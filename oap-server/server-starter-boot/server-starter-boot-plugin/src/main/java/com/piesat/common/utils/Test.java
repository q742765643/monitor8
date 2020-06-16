package com.piesat.common.utils;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * test
 *
 * @author cwh
 * @date 2020年 04月26日 10:53:33
 */
public class Test {
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("HT95279527");
        //要加密的数据（数据库的用户名或密码）
        String username = textEncryptor.encrypt("USR_SOD");
        String password = textEncryptor.encrypt("Pnmic_qwe123");
        System.out.println("username:"+username);
        System.out.println("password:"+password);
        String s1 = textEncryptor.encrypt("Pnmic_qwe123");
        System.out.println("s1:"+s1);
        String s2 = textEncryptor.encrypt("qwe123");
        System.out.println("s2:"+s2);
        String s3 = textEncryptor.encrypt("sod2019");
        System.out.println("s3:"+s3);
        String mmd = textEncryptor.encrypt("nmic@100200");
        System.out.println("mmd:"+mmd);
        String are = textEncryptor.encrypt("music#2020");
        System.out.println("are:"+are);



    }
}
