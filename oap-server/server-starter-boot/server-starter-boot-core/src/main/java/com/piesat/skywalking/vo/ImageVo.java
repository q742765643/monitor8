package com.piesat.skywalking.vo;

import lombok.Data;

/**
 * @ClassName : ImageVo
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-10 16:49
 */
@Data
public class ImageVo {
    private int col1;
    private int col2;
    private int row1;
    private int row2;
    byte[] bytes;
}

