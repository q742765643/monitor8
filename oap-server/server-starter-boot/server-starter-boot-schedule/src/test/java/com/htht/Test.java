package com.htht;

import com.piesat.util.DateExpressionEngine;

import java.util.Date;

/**
 * @ClassName : Test
 * @Description :
 * @Author : zzj
 * @Date: 2021-01-28 17:28
 */
public class Test {
    public static void main(String[] args){
        String folderRegular = DateExpressionEngine.formatDateExpression("/aaaa", new Date().getTime());
        System.out.println(folderRegular);


    }
}

