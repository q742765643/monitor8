package com.piesat.ucenter.web.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.piesat.common.annotation.HtParam;
import com.piesat.common.filter.WrapperedRequest;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.AESUtil;
import com.piesat.common.utils.Doc2PDF;
import com.piesat.common.utils.StringUtils;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.dao.system.UserDao;
import com.piesat.ucenter.entity.system.UserEntity;
import com.piesat.ucenter.rpc.api.system.UserService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 17:13
 */
@RestController
@RequestMapping("/system/user")
@Api(value = "用户操作接口", tags = {"用户操作接口"})
public class UserController {
    @Autowired
    private UserService userService;



    @Value("${serverfile.filePath}")
    private String outFilePath;
    @Value("${fileUpload.httpPath}")
    private String httpPath;

    @ApiOperation(value = "分页查询用户", notes = "分页查询用户")
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(UserDto user, int pageNum, int pageSize) {
        ResultT<PageBean> resultT = new ResultT<>();
        PageForm<UserDto> pageForm = new PageForm<>(pageNum, pageSize, user);
        PageBean pageBean = userService.selectUserList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation(value = "根据用户编号获取详细信息", notes = "根据用户编号获取详细信息")
    @RequiresPermissions("system:user:query")
    @GetMapping(value = "/{userId}")
    public ResultT<UserDto> getInfo(@PathVariable String userId) {
        ResultT<UserDto> resultT = new ResultT<>();
        UserDto userDto = userService.selectUserById(userId);
        resultT.setData(userDto);
        return resultT;
    }

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户", notes = "新增用户")
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<String> add(@RequestBody UserDto user) {
        UserDto userDto = userService.selectUserByUserName(user.getUserName());
        ResultT<String> resultT = new ResultT<>();

        if (null != userDto) {
            resultT.setErrorMessage("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
            return resultT;
        }
        /*if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));*/
        userService.insertUser(user);
        return resultT;
    }

    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户", notes = "修改用户")
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResultT<String> edit(@RequestBody UserDto user) {
        /*userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());*/

        ResultT<String> resultT = new ResultT<>();
        userService.updateUser(user);
        return resultT;
    }
    /**
     * 删除用户
     */
    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public ResultT<String> remove(@PathVariable String[] userIds) {
        ResultT<String> resultT = new ResultT<>();
        List<String> list = new ArrayList();
        if (userIds.length > 0) {
            list = Arrays.asList(userIds);
            userService.deleteUserByIds(list);
        }
        return resultT;
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改", notes = "状态修改")
    @PutMapping("/changeStatus")
    public ResultT<String> changeStatus(@RequestBody UserDto user) {
       /* userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());*/
        ResultT<String> resultT = new ResultT<>();
        userService.updateUserStatus(user);
        return resultT;
    }

    @ApiOperation(value = "用户信息导出", notes = "用户信息导出")
    @RequiresPermissions("system:user:export")
    @GetMapping("/export")
    public void exportExcel(UserDto userDto) {
        userService.exportExcel(userDto);
    }

    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    @ResponseBody
    public ResultT resetPwdSave(@RequestBody UserDto user) {
        UserDto userDto = userService.selectUserById(user.getId());
        String password = new Md5Hash(user.getPassword(), userDto.getUserName(), 2).toString();
        userDto.setPassword(password);
        userService.updateUser(userDto);
        return new ResultT<>();
    }


    @PostMapping(value = "/saveBiz")
//    @RequiresPermissions("system:user:saveBiz")
    @ApiOperation(value = "注册用户申请", notes = "注册用户申请")
    public ResultT saveBiz(HttpServletRequest request, @RequestParam(value = "applyPaper", required = false) MultipartFile applyPaper) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            File newFile = null;
            if (applyPaper != null&& StringUtils.isNotEmpty(applyPaper.getOriginalFilename())) {
                String originalFileName1 = applyPaper.getOriginalFilename();//旧的文件名(用户上传的文件名称)
                //新的文件名
                //String newFileName1 = UUID.randomUUID().toString() + originalFileName1.substring(originalFileName1.lastIndexOf("."));
                String fileSuffix = originalFileName1.substring(originalFileName1.lastIndexOf('.'));
                //上传文件到服务器指定路径
                //转换PDF
                String newFileName1=originalFileName1.substring(0, originalFileName1.lastIndexOf("."))+"_"+new SimpleDateFormat("yyyyMMddHHmms").format(new Date())+fileSuffix;
                newFile = new File(outFilePath + File.separator + newFileName1);
                if (!newFile.getParentFile().exists()) {
                    // 如果目标文件所在的目录不存在，则创建父目录
                    newFile.getParentFile().mkdirs();
                }
                //存入
                applyPaper.transferTo(newFile);
                if (newFile.getName().endsWith(".pdf")||newFile.getName().endsWith(".PDF")){
                    parameterMap.put("pdfPath",new String[]{httpPath + "/filePath/" + newFile.getName()});
                }else{
                    String pdfName = newFileName1.substring(0, newFileName1.lastIndexOf(".")) + ".pdf";
                    String pdfPath = outFilePath + "/" + pdfName;
                    Doc2PDF.doc2pdf(outFilePath + File.separator + newFileName1, pdfPath);
                    parameterMap.put("pdfPath",new String[]{httpPath + "/filePath/" + pdfName});
                }
            }
            ResultT add = userService.addBizUser(parameterMap, newFile == null ? "" : newFile.getPath());
            return add;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }

    @PostMapping(value = "/updateBiz")
//    @RequiresPermissions("system:user:saveBiz")
    @ApiOperation(value = "注册用户申请", notes = "注册用户申请")
    public ResultT updateBiz(HttpServletRequest request, @RequestParam(value = "applyPaper", required = false) MultipartFile applyPaper) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            File newFile = null;
            if (applyPaper != null) {
                String originalFileName1 = applyPaper.getOriginalFilename();//旧的文件名(用户上传的文件名称)
                if(StringUtils.isNotEmpty(originalFileName1)){
                    //新的文件名
                    String newFileName1 = UUID.randomUUID().toString() + originalFileName1.substring(originalFileName1.lastIndexOf("."));
                    newFile = new File(outFilePath + File.separator + newFileName1);
                    // 判断目标文件所在目录是否存在
                    if (!newFile.getParentFile().exists()) {
                        // 如果目标文件所在的目录不存在，则创建父目录
                        newFile.getParentFile().mkdirs();
                    }
                    //存入
                    applyPaper.transferTo(newFile);
                    if (newFile.getName().endsWith(".pdf")||newFile.getName().endsWith(".PDF")){
                        parameterMap.put("pdfPath",new String[]{httpPath + "/filePath/" + newFile.getName()});
                    }else{
                        String pdfName = newFileName1.substring(0, newFileName1.lastIndexOf(".")) + ".pdf";
                        String pdfPath = outFilePath + "/" + pdfName;
                        Doc2PDF.doc2pdf(outFilePath + File.separator + newFileName1, pdfPath);
                        parameterMap.put("pdfPath",new String[]{httpPath + "/filePath/" + pdfName});
                    }
                }
            }
            ResultT add = userService.updateBizUser(parameterMap, newFile == null ? "" : newFile.getPath());
            return add;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "获取注册用户", notes = "获取注册用户")
    @RequiresPermissions("system:dict:gatAllBiz")
    @GetMapping("/gatAllBiz")
    public ResultT<PageBean> gatAllBiz(UserDto userDto,
                                       @HtParam(value = "pageNum", defaultValue = "1") int pageNum,
                                       @HtParam(value = "pageSize", defaultValue = "10") int pageSize) {
        ResultT<PageBean> resultT = new ResultT();
        PageForm<UserDto> pageForm = new PageForm<>(pageNum, pageSize, userDto);
        PageBean pageBean = this.userService.findAllBizUser(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 修改用户（外部状态）
     */
//    @RequiresPermissions("system:user:editBase")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/editBase")
    public ResultT<String> editBase(HttpServletRequest request) {
        String body = null;
        try {
            body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jo = JSONObject.parseObject(body);
        String bizUserid = jo.getString("bizUserid");
        String checked =  jo.getString("checked");
        UserDto user = new UserDto();
        user.setUserName(bizUserid);
        user.setChecked(checked);
        ResultT<String> resultT = new ResultT<>();
        return userService.editBase(user);
    }

    /**
     * 修改用户（外部状态）
     */
//    @RequiresPermissions("system:user:editBase")
    @Log(title = "用户管理--业务用户审核", businessType = BusinessType.UPDATE)
    @GetMapping("/editBaseSod")
    public ResultT<String> editBaseSod(String bizUserid, String checked,String reason) {
//        String body = null;
//        try {
//            body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JSONObject jo = JSONObject.parseObject(body);
//        String bizUserid = jo.getString("bizUserid");
//        String checked =  jo.getString("checked");
        UserDto user = new UserDto();
        user.setUserName(bizUserid);
        user.setChecked(checked);
        user.setReason(reason);
        ResultT<String> resultT = new ResultT<>();
        return userService.editBase(user);
    }

    /**
     * 修改用户信息
     */
//    @RequiresPermissions("system:user:editBase")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/editInfo")
    public ResultT<String> editInfo(@RequestBody UserDto user) {
        return userService.editBase(user);
    }

    /**
     * 修改密码（外部接口）
     */
//    @RequiresPermissions("system:user:updatePwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePwd")
    public ResultT<String> updatePwd(HttpServletRequest request) {
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            JSONObject jo = JSONObject.parseObject(body);
        String bizUserid = jo.getString("bizUserid");
        String newPwd = jo.getString("newPwd");
        String oldPwd = jo.getString("oldPwd");
        UserDto userDto = this.userService.selectUserByUserName(bizUserid);
        if (userDto==null){
            return ResultT.failed("用户不存在");
        }
        String pwd = AESUtil.aesDecrypt(userDto.getPassword()).trim();
        if (pwd.equals(oldPwd)) {
            UserDto user = new UserDto();
            user.setUserName(bizUserid);
            try {
                String s = AESUtil.aesEncrypt(newPwd).trim();
                user.setPassword(s);
                return userService.editPwd(user);
            } catch (Exception e) {
                e.printStackTrace();
                return ResultT.failed(e.getMessage());
            }
        } else {
            return ResultT.failed("旧密码不正确！");
        }
        } catch (IOException e) {
            e.printStackTrace();
            return ResultT.failed("参数不正确！");
        }
    }

    /**
     * 根据用户名获取详细信息（外部接口）
     */
//    @RequiresPermissions("system:user:getUserInfo")
    @GetMapping(value = "/getUserInfo")
    public ResultT<UserDto> getUserInfo(String bizUserid) {
        ResultT<UserDto> resultT = new ResultT<>();
        UserDto userDto = userService.selectUserByUserName(bizUserid);
        resultT.setData(userDto);
        return resultT;
    }

    @RequiresPermissions("system:dict:findAllBizUser")
    @GetMapping("/findAllBizUser")
    public ResultT<PageBean> findAllBizUser(UserDto userDto,
                                  @HtParam(value="pageNum",defaultValue="1") int pageNum,
                                  @HtParam(value="pageSize",defaultValue="10") int pageSize)
    {
        ResultT<PageBean> resultT=new ResultT();
        PageForm<UserDto> pageForm=new PageForm<>(pageNum,pageSize,userDto);
        PageBean pageBean=userService.findAllBizUser(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @RequiresPermissions("system:user:getUserByType")
    @GetMapping(value = "/getUserByType")
    @ApiOperation(value = "根据类型获取用户", notes = "根据类型获取用户")
    public ResultT getAllPortalUser(String userType)
    {
        List<UserDto> userDtos = this.userService.findByUserType(userType);
        return ResultT.success(userDtos);
    }


}
