package com.piesat.util;

import java.lang.reflect.Field;

/**
 * @ClassName : NullUtil
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-13 20:20
 */
public class NullUtil {
    public static void changeToNull(Object o) {
        Class c = o.getClass();
        Class sc = c.getSuperclass();
        try {

            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                if (f.getType() == Integer.class) {
                    f.set(o, null);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

