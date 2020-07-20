package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/21 17:10
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface UserService {
    public UserDto saveUserDto(UserDto userDto);


    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public UserDto selectUserByUserName(String userName);

    /**
     *@描述 根据appId查找用户
     *@参数 [appId]
     *@返回值 com.piesat.ucenter.rpc.dto.system.UserDto
     *@author zzj
     *@创建时间 2019/11/28 16:38
     **/
    public UserDto selectUserByAppId(String appId);

    /**
     * 根据条件分页查询用户列表
     *
     * @param
     * @return 用户信息集合信息
     */
    PageBean selectUserList(PageForm<UserDto> pageForm);

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void insertUser(UserDto user);

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public void updateUser(UserDto user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    public void updateUserStatus(UserDto user);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public void deleteUserByIds(List<String> userIds);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public UserDto selectUserById(String userId);

     String selectUserRoleGroup(String userName);

    public UserDto updateProfile(UserDto user);

    public void exportExcel(UserDto userDto);

    ResultT addBizUser(Map<String, String[]> parameterMap, String applyPaper);

    ResultT updateBizUser(Map<String, String[]> parameterMap, String applyPaper);

    PageBean findAllBizUser(PageForm<UserDto> pageForm);

    ResultT editBase(UserDto user);

    List<UserDto> findByUserType(String userType);

    ResultT editPwd(UserDto user);
}
