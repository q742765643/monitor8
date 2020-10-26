package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : DirectoryAccountDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 14:40
 */
@Data
public class DirectoryAccountDto {
    @ApiModelProperty(value = "共享目录名称")
    private String name;
    @ApiModelProperty(value = "共享目录远程ip")
    private String ip;
    @ApiModelProperty(value = "用户名")
    private String user;
    @ApiModelProperty(value = "密码")
    private String pass;
}

