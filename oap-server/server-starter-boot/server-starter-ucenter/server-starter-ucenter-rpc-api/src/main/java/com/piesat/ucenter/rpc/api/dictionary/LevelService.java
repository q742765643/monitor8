package com.piesat.ucenter.rpc.api.dictionary;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.dictionary.LevelDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

/**
 * @author wangyajuan
 * @description
 * @date 2019年12月23日下午6:05:15
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface LevelService {

    /**
     * 根据条件分页查询
     *
     * @param pageForm
     * @return
     */
    public PageBean selectLevelList(PageForm<LevelDto> pageForm);

    /**
     * 添加层次属性
     *
     * @param levelDto
     * @return
     */
    LevelDto saveDto(LevelDto levelDto);

    /**
     * 批量删除
     *
     * @param levelIds
     */
    public void deleteLevelByIds(List<String> levelIds);

    /**
     * 修改
     *
     * @param levelDto
     * @return
     */
    public LevelDto updateLevel(LevelDto levelDto);

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    LevelDto getDotById(String id);

    List<LevelDto> getAllLevel();
}
