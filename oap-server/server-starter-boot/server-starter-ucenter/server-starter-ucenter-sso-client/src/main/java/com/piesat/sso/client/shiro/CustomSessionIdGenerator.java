package com.piesat.sso.client.shiro;

import java.io.Serializable;
import java.util.UUID;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

/**
 * @ClassName : CustomSessionIdGenerator
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-04 18:37
 */
public class CustomSessionIdGenerator implements SessionIdGenerator {

    @Override
    public Serializable generateId(Session session) {
        if("guest".equals(session.getId())){
            return "guest";
        }
        return "HTHT"+ UUID.randomUUID().toString().replace("-","");
    }
}

