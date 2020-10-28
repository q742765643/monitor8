package com.piesat.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 20:04
 */
@Data
public class HttpReq implements Serializable {
    private String data;
    private String sign;
    private long timestap;

    @Override
    public String toString() {
        return "{\"data\":\"" + data + "\",\"sign\":\"" + sign + "\",\"timestap\":" + timestap + "}";
    }
}
