package com.piesat.skywalking.web.folder;

import com.piesat.skywalking.api.folder.DirectoryAccountService;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName : DirectoryAccountController
 * @Description : 共享目录管理
 * @Author : zzj
 * @Date: 2020-10-27 10:28
 */
@RestController
@Api(value = "共享目录接口", tags = {"共享目录接口"})
@RequestMapping("/directoryAccount")
public class DirectoryAccountController {
    @Autowired
    private DirectoryAccountService directoryAccountService;

    @ApiOperation(value = "分页查询共享目录", notes = "分页查询共享目录")
    @GetMapping("/list")
    public ResultT<PageBean<DirectoryAccountDto>> list(DirectoryAccountDto directoryAccountDto, int pageNum, int pageSize) {
        ResultT<PageBean<DirectoryAccountDto>> resultT = new ResultT<>();
        PageForm<DirectoryAccountDto> pageForm = new PageForm<>(pageNum, pageSize, directoryAccountDto);
        PageBean pageBean = directoryAccountService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询共享目录", notes = "根据ID查询共享目录")
    @GetMapping(value = "/{id}")
    public ResultT<DirectoryAccountDto> getInfo(@PathVariable String id) {
        ResultT<DirectoryAccountDto> resultT = new ResultT<>();
        DirectoryAccountDto directoryAccountDto = directoryAccountService.findById(id);
        resultT.setData(directoryAccountDto);
        return resultT;
    }

    @ApiOperation(value = "查询所有共享目录", notes = "查询所有共享目录")
    @GetMapping(value = "/selectAll")
    public ResultT<List<DirectoryAccountDto>> selectAll() {
        ResultT<List<DirectoryAccountDto>> resultT = new ResultT<>();
        List<DirectoryAccountDto> directoryAccountDtos = directoryAccountService.selectAll();
        resultT.setData(directoryAccountDtos);
        return resultT;
    }

    @ApiOperation(value = "添加或者修改共享目录", notes = "添加或者修改共享目录")
    @PostMapping
    @RequiresPermissions("directory:directoryAccount:add")
    @Log(title = "共享目录管理", businessType = BusinessType.INSERT)
    public ResultT<String> add(@RequestBody DirectoryAccountDto directoryAccountDto) {
        ResultT<String> resultT = new ResultT<>();
        directoryAccountService.save(directoryAccountDto);
        return resultT;
    }

    @ApiOperation(value = "删除文件共享目录", notes = "删除文件共享目录")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("directory:directoryAccount:remove")
    @Log(title = "共享目录管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        directoryAccountService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }
}

