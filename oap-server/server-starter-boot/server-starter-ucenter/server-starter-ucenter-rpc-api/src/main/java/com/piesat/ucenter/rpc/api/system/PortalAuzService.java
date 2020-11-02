package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.PortalAuzDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

/**
 * 用户角色审核
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午11:24:37
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface PortalAuzService {
    /**
     * 获取分页数据
     *
     * @param pageForm
     * @return
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020年2月19日下午1:13:51
     */
    PageBean findPageData(PageForm<PortalAuzDto> pageForm) throws Exception;

    /**
     * 根据id删除
     *
     * @param ids
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020年2月19日下午1:32:08
     */
    void delByIds(String ids) throws Exception;

    /**
     * 更新用户角色状态
     *
     * @param dto
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020年2月19日下午1:40:10
     */
    void update(PortalAuzDto dto) throws Exception;

    /**
     * 保存用户角色
     *
     * @param dto
     * @throws Exception
     * @description
     * @author wlg
     * @date 2020年2月19日下午1:40:13
     */
    void add(PortalAuzDto dto) throws Exception;

}
