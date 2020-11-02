package com.piesat.sso.client.shiro;

import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.sso.client.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;


/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/5 14:20
 */
public class HtSessionManager extends DefaultWebSessionManager {
    private static final String AUTHORIZATION = "Authorization";
    private static final String APP_ID = "appId";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";
    private static String THRID_LOGIN_APP_ID = "THRID_LOGIN_APP_ID:";

    public HtSessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);

        //前端不能传AUTHORIZATION的情况
        /*if(StringUtils.isBlank(id)){
            id = WebUtils.toHttp(request).getHeader("Cookie");
            System.out.println("Cookie："+id);
            if(StringUtils.isNotBlank(id)){
                id = id.split("[=]")[1];
            }
        }
        System.out.println("id2:"+id);*/
        if (!StringUtils.isEmpty(id)) {
            if (id.equals("88888888")) {
                return super.getSessionId(request, response);
            }
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            String appId = WebUtils.toHttp(request).getHeader(APP_ID);
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            boolean has = redisUtil.hasKey(THRID_LOGIN_APP_ID + appId);
            if (has) {
                String appSessionId = (String) redisUtil.get(THRID_LOGIN_APP_ID + appId);
                boolean checkSession = redisUtil.hasKey("shiro:session:" + appSessionId);
                if (checkSession) {
                    request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
                    request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, appSessionId);
                    request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
                    return appSessionId;
                }
            }
            return super.getSessionId(request, response);
        }
    }
}
