package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * 用户权限申请
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午11:18:47
 */
@Data
public class PortalAuzDto extends BaseDto {

    /**
     *
     */
    private static final long serialVersionUID = -6756360663104102790L;
    /**
     * portal登录名
     */
    private String account;
    /**
     * 岗位
     */
    private String post;
    /**
     * 用户中文名
     */
    private String username;
    /**
     * 审核状态
     */
    private String status;
}
