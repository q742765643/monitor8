package com.piesat.ucenter.web.controller.system;

import com.piesat.common.utils.FileUploadUtils;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.sso.client.shiro.HtShiroRealm;
import com.piesat.ucenter.rpc.api.system.DeptService;
import com.piesat.ucenter.rpc.api.system.UserService;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2020-03-20 10:11
 **/
@RestController
@Api(value = "个人信息接口", tags = {"个人信息接口"})
@RequestMapping("/system/user/profile")
public class SysProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private FileUploadUtils fileUploadUtils;
    @Autowired
    private DeptService deptService;

    @ApiOperation(value = "获取用户基本信息", notes = "获取用户基本信息")
    @GetMapping
    public ResultT profile()
    {
        ResultT<Map<String,Object>> resultT=new ResultT<>();
        UserDto userDto = (UserDto) SecurityUtils.getSubject().getPrincipal();
        userDto=userService.selectUserById(userDto.getId());
        DeptDto deptDto=deptService.selectDeptById(userDto.getDeptId());
        userDto.setDept(deptDto);
        Map<String,Object> map=new HashMap<>();
        map.put("user",userDto);
        map.put("roleGroup", userService.selectUserRoleGroup(userDto.getUserName()));

        resultT.setData(map);
        return resultT;
    }

    /**
     * 头像上传
     */
    @ApiOperation(value = "用户头像", notes = "用户头像")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public ResultT<String> avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException
    {
        ResultT<String> resultT=new ResultT<>();
        if (!file.isEmpty())
        {
            try {
                UserDto userDto = (UserDto) SecurityUtils.getSubject().getPrincipal();

                String avatar = fileUploadUtils.upload("image",file,resultT);
                if(!resultT.isSuccess()){
                    return resultT;
                }
                userDto.setAvatar(avatar);
                userDto=userService.saveUserDto(userDto);
                resultT.setData(avatar);
                return resultT;
            } catch (Exception e) {
                return ResultT.failed("上传图片异常，请联系管理员");
            }

        }
        return ResultT.failed("上传图片异常，请联系管理员");
    }
    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户", notes = "修改用户")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<String> updateProfile(@RequestBody UserDto user)
    {
        ResultT<String> resultT=new ResultT<>();
        try {
            user=userService.updateProfile(user);
            return resultT;
        } catch (Exception e) {
            return ResultT.failed("修改个人信息异常，请联系管理员");
        }

    }
    /**
     * 重置密码
     */
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public ResultT<String> updatePwd(String oldPassword, String newPassword)
    {
        ResultT<String> resultT=new ResultT<>();
        try {
            UserDto userDto = (UserDto) SecurityUtils.getSubject().getPrincipal();
            userDto=userService.selectUserById(userDto.getId());
            String userName = userDto.getUserName();
            String password = userDto.getPassword();
            String oldPasswordMd5 = new Md5Hash(oldPassword,userName,2).toString();

            if(!password.equals(oldPasswordMd5)){
                resultT.setErrorMessage("旧密码错误");
                return resultT;
            }
            if (oldPassword.equals(newPassword))
            {
                resultT.setErrorMessage("新密码不能与旧密码相同");
                return resultT;
            }
            String newPasswordMd5 = new Md5Hash(newPassword,userName,2).toString();
            userDto.setPassword(newPasswordMd5);

            userDto=userService.updateProfile(userDto);
            return resultT;
        } catch (Exception e) {
            return ResultT.failed("修改密码异常，请联系管理员");
        }

    }

}

