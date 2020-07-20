package com.piesat.ucenter.dao.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.system.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 16:13
 */
@Repository
public interface UserDao extends BaseDao<UserEntity> {
    UserEntity findByUserName(String userName);

    UserEntity findByAppId(String appId);

    UserEntity findByUserNameAndUserType(String userName,String type);

    List<UserEntity> findByCheckedNot(String checked);

    List<UserEntity> findByWebUsernameLike(String webUsername);

    List<UserEntity> findByUserType(String userType);
}
