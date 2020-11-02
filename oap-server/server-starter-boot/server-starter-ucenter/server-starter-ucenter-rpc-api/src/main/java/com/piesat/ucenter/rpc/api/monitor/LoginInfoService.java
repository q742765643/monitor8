package com.piesat.ucenter.rpc.api.monitor;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

/**
 * Created by zzj on 2019/12/17.
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface LoginInfoService {
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(LoginInfoDto logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param pageForm 访问日志对象
     * @return 登录记录集合
     */
    public PageBean selectLogininforList(PageForm<LoginInfoDto> pageForm);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return
     */
    public void deleteLogininforByIds(String[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();

    public void exportExcel(LoginInfoDto loginInfoDto);
}
