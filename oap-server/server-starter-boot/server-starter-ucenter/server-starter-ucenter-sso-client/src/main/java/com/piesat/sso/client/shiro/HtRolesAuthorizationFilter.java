package com.piesat.sso.client.shiro;

import com.alibaba.fastjson.JSON;
import com.piesat.util.ResultT;
import com.piesat.util.ReturnCodeEnum;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-04-13 14:07
 **/
public class HtRolesAuthorizationFilter extends RolesAuthorizationFilter {
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();
        Subject subject = getSubject(request, response);
        ResultT<String> resultT = new ResultT<>();
        // 没有认证,先返回未认证的json
        if (subject.getPrincipal() == null) {
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_401_ERROR);
        } else {
            // 已认证但没有角色，返回为授权的json
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_403_ERROR);
        }
        out.write(JSON.toJSONString(resultT));
        out.flush();
        out.close();
        return false;
    }

}

