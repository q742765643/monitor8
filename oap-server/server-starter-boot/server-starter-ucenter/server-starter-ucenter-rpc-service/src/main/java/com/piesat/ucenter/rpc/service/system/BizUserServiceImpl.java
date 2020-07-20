package com.piesat.ucenter.rpc.service.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.utils.DateUtils;
import com.piesat.ucenter.dao.system.BizUserDao;
import com.piesat.ucenter.dao.system.DeptDao;
import com.piesat.ucenter.entity.system.BizUserEntity;
import com.piesat.ucenter.entity.system.DeptEntity;
import com.piesat.ucenter.mapper.system.DeptMapper;
import com.piesat.ucenter.rpc.api.system.BizUserService;
import com.piesat.ucenter.rpc.api.system.DeptService;
import com.piesat.ucenter.rpc.dto.system.BizUserDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.system.BizUserMapstruct;
import com.piesat.util.ResultT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据注册用户
 *
 * @author cwh
 * @date 2020年 04月17日 17:26:49
 */
@Service
public class BizUserServiceImpl extends BaseService<BizUserEntity> implements BizUserService {

    @Autowired
    private BizUserDao bizUserDao;
    @Autowired
    private BizUserMapstruct bizUserMapstruct;

    @Override
    public BaseDao<BizUserEntity> getBaseDao() {
        return this.bizUserDao;
    }

    @Override
    public UserDto findByBizUserId(String userId) {
        BizUserEntity bizUserEntity = this.bizUserDao.findByBizUserIdAndChecked(userId, "3");
        UserDto user = null;
        if (bizUserEntity != null) {
            user = new UserDto();
            user.setId(bizUserEntity.getBizUserId());
            user.setUserName(bizUserEntity.getBizUserId());
            user.setPassword(bizUserEntity.getPassword());
        }
        return user;
    }

    @Override
    public ResultT add(Map<String, String[]> parameterMap, String applyPaper) {

        Map<String, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getValue().length > 0) {
                map.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        String appNames = map.get("appName");
        String bizUserid = map.get("bizUserid");
        BizUserEntity byBizUserId = this.bizUserDao.findByBizUserId(bizUserid);
        if (byBizUserId != null) {
            return ResultT.failed("业务用户注册id已存在！");
        }
        String password = map.get("password");
        try {
            password = AESUtil.aesEncrypt(password).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String bizType = map.get("bizType");
        String bizIp = map.get("bizIp");
        String validTime = map.get("validTime");
        String remark = map.get("remark");
        String webUsername = map.get("webUsername");
        String legalUnits = map.get("legalUnits");
        String deptName = map.get("deptName");
        String phone = map.get("phone");
        String tutorName = map.get("tutorName");
        String tutorPhone = map.get("tutorPhone");
        String webUserid = map.get("webUserid");
//        String appCoin = map.get("appCoin");
//        String applyAuthority = map.get("applyAuthority");
//        String deptId = map.get("deptId");
//        String loginName = map.get("loginName");
//        String userId = map.get("userId");
//        String interfaceId = map.get("interfaceId");
//        String nonce = map.get("nonce");
//        String timestamp = map.get("timestamp");
        BizUserEntity bizUserEntity = new BizUserEntity();
        bizUserEntity.setApplyPaper(applyPaper);
        bizUserEntity.setApplyTime(new Date());
        bizUserEntity.setAppName(appNames);
        bizUserEntity.setBizIp(bizIp);
        bizUserEntity.setBizType(bizType);
        bizUserEntity.setBizUserId(bizUserid);
        bizUserEntity.setChecked("0");
        bizUserEntity.setDeptName(deptName);
        bizUserEntity.setLastEditTime(new Date());
        bizUserEntity.setLegalUnits(legalUnits);
        bizUserEntity.setPassword(password);
        bizUserEntity.setPhone(phone);
        bizUserEntity.setRemark(remark);
        bizUserEntity.setTutorName(tutorName);
        bizUserEntity.setTutorPhone(tutorPhone);
        Date date = DateUtils.dateTime("yyyy-MM-dd", validTime);
        bizUserEntity.setValidTime(date);
        bizUserEntity.setWebUserId(webUserid);
        bizUserEntity.setWebUsername(webUsername);
        this.bizUserDao.saveNotNull(bizUserEntity);
        return ResultT.success();
    }
}
