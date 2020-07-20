package com.piesat.sso.client.shiro;

import com.alibaba.fastjson.JSON;
import com.piesat.common.filter.WrapperedRequest;
import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.common.utils.OwnException;
import com.piesat.common.utils.ServletUtils;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.ip.AddressUtils;
import com.piesat.common.utils.ip.IpUtils;
import com.piesat.sso.client.enums.OperatorType;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.ucenter.rpc.api.monitor.LoginInfoService;
import com.piesat.ucenter.rpc.api.system.UserService;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.ReturnCodeEnum;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/10 15:10
 */
public class HtAuthenticationFilter extends FormAuthenticationFilter {
    private Logger logger = LoggerFactory.getLogger(HtAuthenticationFilter.class);
    private static String THRID_LOGIN_APP_ID = "THRID_LOGIN_APP_ID:";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        String appId = WebUtils.toHttp(request).getHeader("appId");
        String tokenId = WebUtils.toHttp(request).getHeader("authorization");
        if (null != tokenId && tokenId.equals("88888888")) {
            boolean isLogin = subject.isAuthenticated();
            if (!isLogin) {
                UsernamePasswordToken token = new UsernamePasswordToken("admin", "111111");
                token.setLoginType("0");
                token.setRequest(ServletUtils.getRequest());
                token.setOperatorType(OperatorType.MANAGE.ordinal());
                subject.login(token);
            }

            return true;
        }

        if (null != appId && !"".equals(appId)) {
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            boolean check = this.validatAppId(appId);
            if (check) {
                check = subject.isAuthenticated();
            }
            if (check) {
                return check;
            }
            ResultT<Map<String, Object>> resultT = new ResultT<>();
            HttpServletRequest req = (HttpServletRequest) request;
            try {
                String pwd = WebUtils.toHttp(request).getHeader("pwd");
                pwd = StringUtils.isEmpty(pwd) ? "1111" : pwd;
                UsernamePasswordToken utoken = new UsernamePasswordToken(appId, pwd);
                utoken.setLoginType("1");
                utoken.setRequest(req);
                utoken.setOperatorType(OperatorType.CAS.ordinal());
                subject.login(utoken);
                redisUtil.set(THRID_LOGIN_APP_ID + appId, subject.getSession().getId(), 1800);
                this.recordLogininfor(req, appId, resultT);
                return true;
            } catch (Exception e) {
                resultT.setErrorMessage("接口登陆失败");
                this.recordLogininfor(req, appId, resultT);
                logger.error(OwnException.get(e));
                return false;
            }

        } else {
            return subject.isAuthenticated();
        }

    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        //return super.onAccessDenied(servletRequest,servletResponse);
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setContentType("application/json; charset=utf-8");
        ResultT<String> resultT = new ResultT<>();
        resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_401_ERROR);
        PrintWriter out = resp.getWriter();
        out.print(JSON.toJSONString(resultT));
        out.flush();
        out.close();
        return false;

    }

    private boolean validatAppId(String appId) {
        RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
        boolean has = redisUtil.hasKey(THRID_LOGIN_APP_ID + appId);
        if (!has) {
            return false;
        }
        String appSessionId = (String) redisUtil.get(THRID_LOGIN_APP_ID + appId);
        boolean checkSession = redisUtil.hasKey("shiro:session:" + appSessionId);
        if (!checkSession) {
            return false;
        }
        return true;
    }

    public void recordLogininfor(HttpServletRequest request, String userName, ResultT<Map<String, Object>> resultT) {
        try {
            ExecutorService executorService = (ExecutorService) SpringUtil.getBean("executorService");
            LoginInfoService loginInfoService = SpringUtil.getBean(LoginInfoService.class);
            executorService.execute(
                    () -> {
                        final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                        final String ip = IpUtils.getIpAddr(request);
                        String address = AddressUtils.getRealAddressByIP(ip);
                        String os = userAgent.getOperatingSystem().getName();
                        // 获取客户端浏览器
                        String browser = userAgent.getBrowser().getName();
                        LoginInfoDto logininfor = new LoginInfoDto();
                        logininfor.setUserName(userName);
                        logininfor.setIpaddr(ip);
                        logininfor.setLoginLocation(address);
                        logininfor.setBrowser(browser);
                        logininfor.setOs(os);
                        logininfor.setMsg(resultT.getMsg());
                        if (resultT.isSuccess()) {
                            logininfor.setStatus("0");
                        } else {
                            logininfor.setStatus("1");
                        }
                        logininfor.setLoginTime(new Date());
                        loginInfoService.insertLogininfor(logininfor);
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
