package com.piesat.ucenter.web.controller.system;

import com.alibaba.fastjson.JSON;
import com.piesat.common.constant.Constants;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.utils.IdUtils;
import com.piesat.common.utils.ServletUtils;
import com.piesat.common.utils.VerifyCodeUtils;
import com.piesat.common.utils.ip.AddressUtils;
import com.piesat.common.utils.ip.IpUtils;
import com.piesat.common.utils.sign.Base64;
import com.piesat.sso.client.enums.OperatorType;
import com.piesat.sso.client.util.RedisUtil;
import com.piesat.ucenter.rpc.api.monitor.LoginInfoService;
import com.piesat.ucenter.rpc.api.system.MenuService;
import com.piesat.ucenter.rpc.api.system.RoleService;
import com.piesat.ucenter.rpc.api.system.UserService;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.util.RouterVo;
import com.piesat.util.ResultT;
import com.piesat.util.ReturnCodeEnum;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 11:43
 */
@RestController
@Api(value="用户登录",tags={"用户登录"})
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LoginInfoService loginInfoService;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @param
     * @param uuid 唯一标识
     * @return 结果
     */
    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("/login")
    public ResultT<Map<String,Object>> login(String username, String password, String code, String uuid)
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=new HashMap<>();
        try {
            String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

            String ycode= null;
            try {
                ycode = (String) redisUtil.get(verifyKey);
            } catch (Exception e) {
                resultT.setErrorMessage("验证码错误");
                return resultT;
            }
            if(null==code){
                resultT.setErrorMessage("验证码不能为空");
                return resultT;
            }
            if(ycode==null){
                resultT.setErrorMessage("验证码过期");
                return resultT;
            }
            if(!ycode.equals(code.toUpperCase())){
                resultT.setErrorMessage("验证码错误");
                return resultT;
            }
            UserDto userDto = userService.selectUserByUserName(username);
            if(userDto != null && "11".equals(userDto.getUserType()) && !"3".equals(userDto.getChecked())){
                resultT.setErrorMessage("业务账户未激活");
                return resultT;
            }
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setLoginType("0");
            token.setRequest(ServletUtils.getRequest());
            token.setOperatorType(OperatorType.MANAGE.ordinal());
            subject.login(token);
            map.put("token", subject.getSession().getId());
            resultT.setData(map);
        } catch (LockedAccountException e) {
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_402_ERROR);
        }catch (UnknownAccountException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_404_ERROR);
        }catch (IncorrectCredentialsException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }catch (AuthenticationException ex){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }finally {
            this.recordLogininfor(ServletUtils.getRequest(),username,resultT);
        }
        return resultT;
    }
    @ApiOperation(value = "第三方登录", notes = "第三方登录")
    @PostMapping("/thirdLogin")
    public ResultT<Map<String,Object>> thirdLogin(@RequestBody Map<String,String> userDto)
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=new HashMap<>();
        String appId= userDto.get("appId");
        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(appId, "");
            token.setLoginType("1");
            token.setRequest(ServletUtils.getRequest());
            token.setOperatorType(OperatorType.THRID.ordinal());
            token.setParam(JSON.toJSONString(userDto));
            subject.login(token);
            map.put("token", subject.getSession().getId());
            resultT.setData(map);
        } catch (LockedAccountException e) {
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_402_ERROR);
        }catch (UnknownAccountException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_404_ERROR);
        }catch (IncorrectCredentialsException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }catch (AuthenticationException ex){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }finally {
            this.recordLogininfor(ServletUtils.getRequest(),appId,resultT);
        }
        return resultT;
    }

    @ApiOperation(value = "第三方登录", notes = "第三方登录")
    @GetMapping("/getToken")
    public ResultT<Map<String,Object>> getToken()
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=new HashMap<>();
        try {
            Subject subject = SecurityUtils.getSubject();
            UserDto loginUser = (UserDto) subject.getPrincipal();
            map.put("token", subject.getSession().getId());
            map.put("userId", loginUser.getUserName());
            String password = AESUtil.aesDecrypt(loginUser.getPassword()).trim();
            map.put("pwd",password);
            resultT.setData(map);
        } catch (LockedAccountException e) {
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_402_ERROR);
        }catch (UnknownAccountException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_404_ERROR);
        }catch (IncorrectCredentialsException e){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }catch (AuthenticationException ex){
            resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_405_ERROR);
        }
        return resultT;
    }
    /**
     * 校验token
     */
    @ApiOperation(value = "校验token", notes = "校验token")
    @GetMapping(value = "checkToken/{token}")
    public ResultT<Map<String,Object>> checkToken(@PathVariable String token)
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=new HashMap<>();
        map.put("token", token);
        resultT.setData(map);
        return resultT;
    }
    @ApiOperation(value = "获取用户基本信息和权限", notes = "获取用户基本信息和权限")
    @GetMapping("getInfo")
    public ResultT<Map<String,Object>> getInfo()
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        Map<String,Object> map=new HashMap<>();

        UserDto userDto = (UserDto)SecurityUtils.getSubject().getPrincipal();
        Set<String> roles=null;
        Set<String> permissions=null;
        if(userDto!=null) {
            // 角色集合
             roles = roleService.getRolePermission(userDto);
             permissions = menuService.getMenuPermission(userDto);
        }


        map.put("user", userDto);
        map.put("roles", roles);
        map.put("permissions", permissions);
        resultT.setData(map);
        return resultT;
    }
    @ApiOperation(value = "获取vue路由列表", notes = "获取vue路由列表")
    @GetMapping("getRouters")
    public ResultT<List<RouterVo>>  getRouters()
    {
        ResultT<List<RouterVo>> resultT=new ResultT<>();
        UserDto userDto = (UserDto)SecurityUtils.getSubject().getPrincipal();
        List<RouterVo> routerVos=menuService.getRouters(userDto.getId());
        resultT.setData(routerVos);
        return resultT;
    }
 /*   @GetMapping(value = "/unauth")
    public ResultT<String> unauth() {
        ResultT<String> resultT=new ResultT<>();
        resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_401_ERROR);
        return resultT;
    }
    @GetMapping(value = "/unauthorized")
    public ResultT<String> unauthorized() {
        ResultT<String> resultT=new ResultT<>();
        resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_403_ERROR);
        return resultT;
    }*/

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    public ResultT<Map<String,Object>> getCode(HttpServletResponse response) throws IOException
    {
        ResultT<Map<String,Object>> resultT = new ResultT();

        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        // 唯一标识
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        redisUtil.set(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION*60);
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try
        {
            Map<String,Object> map=new HashMap<>();
            map.put("uuid", uuid);
            map.put("code", verifyCode);
            map.put("img", Base64.encode(stream.toByteArray()));
            resultT.setData(map);
            return resultT;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            resultT.setErrorMessage(e.getMessage());
            return resultT;
        }
        finally
        {
            stream.close();
        }
    }
    @ApiOperation(value = "登出", notes = "登出")
    @PostMapping("/logout")
    public ResultT<String> logout(HttpServletResponse response){
        ResultT<String> resultT=new ResultT<>();
        try {
            Subject currentUser = SecurityUtils.getSubject();//获取当前用户信息
            if(currentUser.isAuthenticated()){
                currentUser.logout();
                resultT.setMessage("退出成功");
            }
            resultT.setMessage("退出成功");

           /* response.setHeader( "Access-Control-Allow-Origin", "*" );
            // 允许请求的方法
            response.setHeader( "Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT" );
            // 多少秒内，不需要再发送预检验请求，可以缓存该结果
            response.setHeader( "Access-Control-Max-Age", "3600" );
            // 表明它允许跨域请求包含xxx头
            response.setHeader( "Access-Control-Allow-Headers", "*" );
            //是否允许浏览器携带用户身份信息（cookie）
            response.setHeader( "Access-Control-Allow-Credentials", "true" );
            response.setHeader( "Access-Control-Expose-Headers", "Content-disposition" );*/
        } catch (Exception e) {
            resultT.setErrorMessage("退出失败");
        }
        return resultT;
    }
    public  static void main(String[] args ){
        Map<String,String> map=new HashMap<>();
        map.put("userId","11111111111");
        map.put("userName","222");
        try {
           String aa= AESUtil.aesEncrypt(JSON.toJSONString(map));
           System.out.println(aa);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recordLogininfor(HttpServletRequest request,String userName,ResultT<Map<String,Object>> resultT){
        try {
            executorService.execute(
                    ()->{
                        final UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                        final String ip = IpUtils.getIpAddr(request);
                        String address = AddressUtils.getRealAddressByIP(ip);
                        String os = userAgent.getOperatingSystem().getName();
                        // 获取客户端浏览器
                        String browser = userAgent.getBrowser().getName();
                        LoginInfoDto logininfor=new LoginInfoDto();
                        logininfor.setUserName(userName);
                        logininfor.setIpaddr(ip);
                        logininfor.setLoginLocation(address);
                        logininfor.setBrowser(browser);
                        logininfor.setOs(os);
                        logininfor.setMsg(resultT.getMsg());
                        if(resultT.isSuccess()){
                            logininfor.setStatus("0");
                        }else{
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
