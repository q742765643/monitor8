package com.piesat.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-04-10 09:57
 **/
@Data
public class CasVo implements Serializable {
    private long timestamp;
    private String nonce;
    private String sign;
    private String data;
    private String userId;
    private String pwd;

}

