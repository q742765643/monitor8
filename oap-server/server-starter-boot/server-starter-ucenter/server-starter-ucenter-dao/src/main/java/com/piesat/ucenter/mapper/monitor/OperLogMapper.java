package com.piesat.ucenter.mapper.monitor;

import com.piesat.ucenter.entity.monitor.OperLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 11:27
 **/
@Component
public interface OperLogMapper {
    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<OperLogEntity> selectOperLogList(OperLogEntity operLog);

}

