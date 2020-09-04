package com.piesat.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @program: backupandclear
 * @描述
 * @创建人 zzj
 * @创建时间 2019/5/29 16:52
 */
public class OwnException {
    public static String get(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

}
