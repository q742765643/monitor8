package com.piesat.ucenter.rpc.api.dictionary;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.dictionary.DefineDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

/**
 * @author yaya
 * @description TODO
 * @date 2019/12/26 16:53
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface DefineService {

    /**
     * 根据条件分页查询
     * @param pageForm
     * @return
     */
    public PageBean selectDefineList(PageForm<DefineDto> pageForm);

    /**
     * 添加区域类别
     * @param defineDto
     * @return
     */
    DefineDto saveDto(DefineDto defineDto);

    /**
     * 批量删除
     * @param defineIds
     */
    public void deleteDefineByIds(List<String> defineIds);

    /**
     * 修改
     * @param defineDto
     * @return
     */
    public DefineDto updateDefine(DefineDto defineDto);

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    DefineDto getDotById(String id);


    List<DefineDto> all();

    void exportExcel(DefineDto defineDto);

    List<DefineDto> findByParam(DefineDto defineDto);
}
