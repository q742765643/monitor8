package com.piesat.ucenter.web.controller.system;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.util.StringUtil;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.system.PortalAuzService;
import com.piesat.ucenter.rpc.dto.system.PortalAuzDto;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/** 业务用户 , 用户角色审核
*@description
*@author wlg
*@date 2020年2月19日上午11:35:03
*
*/
@RestController
@RequestMapping("/system/portalAuz")
@Api(value="用户角色审核",tags= {"用户角色审核接口"})
@Slf4j
public class PortalAuzController {
	
	@Autowired
	private PortalAuzService portalAuzService;
	/**
	 *  获取分页数据
	 * @description 
	 * @author wlg
	 * @date 2020年2月19日下午1:11:57
	 * @param portalAuzDto
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequiresPermissions("system:portalAuz:page")
	@ApiOperation(value="分页查询",notes="获取分页数据")
	@GetMapping(value="/page")
	public ResultT findPage(PortalAuzDto portalAuzDto,int pageNum,int pageSize) {
		log.info(">>>>>>获取用户角色审核分页数据");
		try {
			PageForm<PortalAuzDto> pageForm = new PageForm<>(pageNum,pageSize,portalAuzDto);
			PageBean page = portalAuzService.findPageData(pageForm);
			return ResultT.success(page);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultT.failed(e.getMessage());
		}
	}
	/**
	 *  删除业务角色
	 * @description 
	 * @author wlg
	 * @date 2020年2月19日下午1:35:33
	 * @param ids
	 * @return
	 */
	@ApiOperation(value="删除业务角色",notes="删除业务角色接口,id逗号拼接")
	@DeleteMapping(value="/delByIds")
	@RequiresPermissions("system:portalauz:delByIds")
    @Log(title = "用户角色授权", businessType = BusinessType.DELETE)
	public ResultT delByIds(String ids) {
		log.info(">>>>>>根据id删除用户角色");
		try {
			if(StringUtil.isEmpty(ids)) return ResultT.failed("参数不能为空");
			portalAuzService.delByIds(ids);
			return ResultT.success("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultT.failed(e.getMessage());
		}
	}
	/**
	 *  更改业务用户状态
	 * @description 
	 * @author wlg
	 * @date 2020年2月19日下午1:39:21
	 * @param portalAuzDto
	 * @return
	 */
	@ApiOperation(value="通过/不通过 ",notes="更改用户角色状态通过/不通过")
	@PutMapping(value="/update")
	@RequiresPermissions("system:portalauz:update")
    @Log(title = "用户角色授权", businessType = BusinessType.UPDATE)
	public ResultT update(PortalAuzDto portalAuzDto) {
		log.info(">>>>>>审核用户角色 业务用户");
		try {
			portalAuzService.update(portalAuzDto);
			return ResultT.success("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultT.failed(e.getMessage());
		}
	}
	/**
	 *  新增用户业务角色
	 * @description 
	 * @author wlg
	 * @date 2020年2月19日下午1:46:53
	 * @param portalAuzDto
	 * @return
	 */
	@ApiOperation(value="新增用户角色 ",notes="新增用户角色接口")
	@PostMapping(value="/add")
	@RequiresPermissions("system:portalauz:add")
    @Log(title = "用户角色授权", businessType = BusinessType.INSERT)
	public ResultT add(PortalAuzDto portalAuzDto) {
		log.info(">>>>>>新增用户业务角色");
		try {
			portalAuzService.add(portalAuzDto);
			return ResultT.success("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultT.failed(e.getMessage());
		}
	}
}
