package com.piesat.ucenter.web.controller.system;

import com.piesat.ucenter.rpc.api.system.BizUserService;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * 注册用户
 *
 * @author cwh
 * @date 2020年 04月20日 10:55:15
 */
@RestController
@Api(value = "注册用户", tags = {"注册用户"})
@RequestMapping("/system/bizuser")
public class BizUserController {

    @Autowired
    private BizUserService bizUserService;

    @Value("${fileUpload.httpPath}")
    private String outFilePath;

    @PostMapping(value = "/save")
    @RequiresPermissions("system:bizuser:list")
    @ApiOperation(value = "添加", notes = "添加")
    public ResultT save(HttpServletRequest request, @RequestParam(value = "applyPaper", required = false) MultipartFile applyPaper) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            File newFile = null;
            if (applyPaper != null) {
                String originalFileName1 = applyPaper.getOriginalFilename();//旧的文件名(用户上传的文件名称)
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
            }
            ResultT add = bizUserService.add(parameterMap, newFile == null ? "" : newFile.getPath());
            return add;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultT.failed(e.getMessage());
        }
    }
}
