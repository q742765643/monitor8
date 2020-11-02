package com.piesat.ucenter.mapper.system;

import com.piesat.common.annotation.MybatisAnnotation;
import com.piesat.ucenter.entity.system.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 18:28
 */
@Component
public interface UserMapper {
    UserEntity selectByPrimaryKey(String id);

    @MybatisAnnotation(value = "")
    List<UserEntity> selectUserList(UserEntity userEntity);

}
