package com.piesat.sso.client.shiro;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.utils.ip.IpUtils;
import com.piesat.sso.client.util.AddressUtils;
import com.piesat.ucenter.rpc.api.system.MenuService;
import com.piesat.ucenter.rpc.api.system.RoleService;
import com.piesat.ucenter.rpc.api.system.UserService;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.Date;
import java.util.Set;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/5 14:36
 */
@Slf4j
public class HtShiroRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserDto userDto = (UserDto) principalCollection.getPrimaryPrincipal();
        RoleService roleService = SpringUtil.getBean(RoleService.class);
        MenuService menuService = SpringUtil.getBean(MenuService.class);

        // 角色集合
        Set<String> roles = roleService.getRolePermission(userDto);
        authorizationInfo.addRoles(roles);
        Set<String> permissions = menuService.getMenuPermission(userDto);
        authorizationInfo.addStringPermissions(permissions);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = (String) authenticationToken.getPrincipal();
        ByteSource salt = ByteSource.Util.bytes(username);
        UserService userService = SpringUtil.getBean(UserService.class);
        UserDto userDto = null;
        if ("0".equals(token.getLoginType())) {
            userDto = userService.selectUserByUserName(username);
            if (userDto == null) {
                throw new UnknownAccountException();
            }
            if (userDto.getStatus().equals("1")) { //账户冻结
                throw new LockedAccountException();
            }
            if (null != token.getRequest()) {
                try {
                    UserAgent userAgent = UserAgent.parseUserAgentString(token.getRequest().getHeader("User-Agent"));
                    String ip = IpUtils.getIpAddr(token.getRequest());
                    userDto.setLoginIp(ip);
                    userDto.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
                    userDto.setBrowser(userAgent.getBrowser().getName());
                    userDto.setOs(userAgent.getOperatingSystem().getName());
                } catch (Exception e) {
                    log.info("用户信息=="+ JSON.toJSONString(userDto));
                    e.printStackTrace();
                }
            }
            userDto.setParams(token.getParam());
            userDto.setOperatorType(token.getOperatorType());
            userDto.setLoginDate(new Date());
            SimpleAuthenticationInfo    authenticationInfo = new SimpleAuthenticationInfo(
                        userDto, //用户名
                        userDto.getPassword(), //密码
                        salt,
                        getName()  //realm name
                );


            return authenticationInfo;
        }


        if ("1".equals(token.getLoginType())) {
            userDto = userService.selectUserByUserName(username);
            String pwd = AESUtil.aesDecrypt(userDto.getPassword()).trim();
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    userDto, //用户名
                    new Md5Hash(pwd, username, 2).toString(), //密码
                    salt,
                    getName());
            return authenticationInfo;
        }
        if ("2".equals(token.getLoginType())) {
            userDto=new UserDto();
            userDto.setId("guest");
            userDto.setUserName("guest");
            userDto.setPassword("");
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    userDto, //用户名
                    new Md5Hash("guest", username, 2).toString(), //密码
                    salt,
                    getName());
            return authenticationInfo;
        }

        return null;
    }

}
