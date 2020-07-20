package com.piesat.ucenter.mapper.monitor;

import com.piesat.ucenter.entity.monitor.LoginInfoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zzj on 2019/12/17.
 */
@Component
public interface LoginInfoMapper {
    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public List<LoginInfoEntity> selectLogininforList(LoginInfoEntity logininfor);

}
