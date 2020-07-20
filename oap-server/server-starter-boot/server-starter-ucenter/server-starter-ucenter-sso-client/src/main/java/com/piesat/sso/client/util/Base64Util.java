package com.piesat.sso.client.util;

import java.util.Base64;

/**
 * @author cwh
 * @date 2020年 05月03日 10:26:55
 */
public class Base64Util {

    public static final  String salt = "!{sod}";

    public static String getEncoder(String password){
        String pass = password + salt;
        String pwd = new String(Base64.getEncoder().encode(pass.getBytes()));
        return pwd;
    }

    public static String getDecoder(String password){
        String pwd = new String( Base64.getDecoder().decode(password));
        pwd = pwd.replace(salt,"");
        return pwd;
    }
}
