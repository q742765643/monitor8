package com.piesat.ucenter.rpc.api.monitor;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.monitor.OperLogDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 15:34
 **/
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface OperLogService {
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    public void insertOperlog(OperLogDto operLog);


    /**
     * 查询系统操作日志集合
     *
     * @param pageForm 操作日志对象
     * @return 操作日志集合
     */
    public PageBean selectOperLogList(PageForm<OperLogDto> pageForm);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public void deleteOperLogByIds(String[] operIds);


    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public OperLogDto selectOperLogById(String operId);


    /**
     * 清空操作日志
     */
    public void cleanOperLog();


    public void exportExcel(OperLogDto operLogDto);

    List<OperLogDto> findByOperNameAndTitle(String operName, String title);

}

