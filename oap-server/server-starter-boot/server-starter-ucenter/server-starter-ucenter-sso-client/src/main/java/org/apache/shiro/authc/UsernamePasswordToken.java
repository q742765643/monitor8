package org.apache.shiro.authc;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/9 16:47
 */
public class UsernamePasswordToken implements HostAuthenticationToken, RememberMeAuthenticationToken {
    private String username;
    private char[] password;
    private boolean rememberMe;
    private String host;
    private String loginType;
    private HttpServletRequest request;
    private String param;
    private int operatorType;
    public UsernamePasswordToken() {
        this.rememberMe = false;
    }

    public UsernamePasswordToken(String username, char[] password) {
        this(username, (char[]) password, false, (String) null);
    }

    public UsernamePasswordToken(String username, String password) {
        this(username, (char[]) (password != null ? password.toCharArray() : null), false, (String) null);
    }

    public UsernamePasswordToken(String username, char[] password, String host) {
        this(username, password, false, host);
    }

    public UsernamePasswordToken(String username, String password, String host) {
        this(username, password != null ? password.toCharArray() : null, false, host);
    }

    public UsernamePasswordToken(String username, char[] password, boolean rememberMe) {
        this(username, (char[]) password, rememberMe, (String) null);
    }

    public UsernamePasswordToken(String username, String password, boolean rememberMe) {
        this(username, (char[]) (password != null ? password.toCharArray() : null), rememberMe, (String) null);
    }

    public UsernamePasswordToken(String username, char[] password, boolean rememberMe, String host) {
        this.rememberMe = false;
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
        this.host = host;
    }


    public UsernamePasswordToken(String username, String password, boolean rememberMe, String host) {
        this(username, password != null ? password.toCharArray() : null, rememberMe, host);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
    @Override
    public Object getPrincipal() {
        return this.getUsername();
    }
    @Override
    public Object getCredentials() {
        return this.getPassword();
    }
    @Override
    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    @Override
    public boolean isRememberMe() {
        return this.rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void clear() {
        this.username = null;
        this.host = null;
        this.rememberMe = false;
        if (this.password != null) {
            for (int i = 0; i < this.password.length; ++i) {
                this.password[i] = 0;
            }

            this.password = null;
        }

    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(" - ");
        sb.append(this.username);
        sb.append(", rememberMe=").append(this.rememberMe);
        if (this.host != null) {
            sb.append(" (").append(this.host).append(")");
        }

        return sb.toString();
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }
}