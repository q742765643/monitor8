package com.piesat.ucenter.rpc.service.monitor;

import com.piesat.common.utils.StringUtils;
import com.piesat.ucenter.entity.system.DeptEntity;
import com.piesat.ucenter.mapper.system.DeptMapper;
import com.piesat.ucenter.rpc.api.monitor.OnlineService;
import com.piesat.ucenter.rpc.dto.monitor.OnlineDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/9 10:38
 */
@Service
public class OnlineServiceImpl implements OnlineService {
    @Autowired
    private SessionDAO sessionDAO;
    @Autowired
    private DeptMapper deptMapper;

    @Override
    public PageBean list(String ipaddr, String userName, int pageNum, int pageSize) {
        Collection<Session> keys = sessionDAO.getActiveSessions();
        List<OnlineDto> userOnlineList = new ArrayList<OnlineDto>();
        for (Session key : keys) {
            SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
            if (key == null || key.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
               continue;
            } else {
                principalCollection = (SimplePrincipalCollection) key.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                UserDto userDto = (UserDto) principalCollection.getPrimaryPrincipal();
                userDto.setTokenId(key.getId().toString());
                userDto.setLoginDate(key.getStartTimestamp());
                userDto.setLoginIp(key.getHost());
                //userDto.setLoginIp(key.getHost());
                if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                    if (StringUtils.equals(ipaddr, key.getHost()) && StringUtils.equals(userName, userDto.getUserName())) {
                        userOnlineList.add(this.selectOnlineByInfo(ipaddr, userName, userDto));
                    }
                } else if (StringUtils.isNotEmpty(ipaddr)) {
                    if (StringUtils.equals(ipaddr, key.getHost())) {
                        userOnlineList.add(this.selectOnlineByIpaddr(ipaddr, userDto));
                    }
                } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(userDto)) {
                    if (StringUtils.equals(userName, userDto.getUserName())) {
                        userOnlineList.add(this.selectOnlineByUserName(userName, userDto));
                    }
                } else {
                    userOnlineList.add(this.loginUserDtoToUserOnline(userDto));
                }
            }
        }
        userOnlineList.removeAll(Collections.singleton(null));
        Collections.sort(userOnlineList);
        Collections.reverse(userOnlineList);
        int startIndex = (pageNum -1)* pageSize;
        int endIndex = ((pageNum -1) * pageSize) + pageSize;
        int size = userOnlineList.size();
        if (endIndex > size) {
            endIndex = size;
        }
        PageBean pageBean = new PageBean();
        pageBean.setTotalCount(size);
        pageBean.setPageData(userOnlineList.subList(startIndex, endIndex));
        return pageBean;
    }
    public void forceLogout(String tokenId){
        Session session = sessionDAO.readSession(tokenId);//通过readSession读取session，然后调用delete删除
        sessionDAO.delete(session);
    }
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user   用户信息
     * @return 在线用户信息
     */

    public OnlineDto selectOnlineByIpaddr(String ipaddr, UserDto user) {
        if (StringUtils.equals(ipaddr, user.getLoginIp())) {
            return loginUserDtoToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user     用户信息
     * @return 在线用户信息
     */

    public OnlineDto selectOnlineByUserName(String userName, UserDto user) {
        if (StringUtils.equals(userName, user.getUserName())) {
            return loginUserDtoToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr   登录地址
     * @param userName 用户名称
     * @param user     用户信息
     * @return 在线用户信息
     */

    public OnlineDto selectOnlineByInfo(String ipaddr, String userName, UserDto user) {
        if (StringUtils.equals(ipaddr, user.getLoginIp()) && StringUtils.equals(userName, user.getLoginIp())) {
            return loginUserDtoToUserOnline(user);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    public OnlineDto loginUserDtoToUserOnline(UserDto user) {
        if (StringUtils.isNull(user)) {
            return null;
        }
        DeptEntity deptEntity=deptMapper.getById(user.getDeptId());
        OnlineDto OnlineDto = new OnlineDto();
        OnlineDto.setTokenId(user.getTokenId());
        OnlineDto.setUserName(user.getUserName());
        OnlineDto.setIpaddr(user.getLoginIp());
        OnlineDto.setLoginLocation(user.getLoginLocation());
        OnlineDto.setBrowser(user.getBrowser());
        OnlineDto.setOs(user.getOs());
        OnlineDto.setLoginTime(user.getLoginDate());
        if(deptEntity!=null){
            OnlineDto.setDeptName(deptEntity.getDeptName());
        }
        if (StringUtils.isNotNull(user.getDept())) {
            OnlineDto.setDeptName(user.getDept().getDeptName());
        }
        return OnlineDto;
    }

}
