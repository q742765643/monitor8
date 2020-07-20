package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.DictTypeDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:55
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface DictTypeService {
    /**
     * 根据条件分页查询字典类型
     *
     * @param pageForm 字典类型信息
     * @return 字典类型集合信息
     */
    public PageBean selectDictTypeList(PageForm<DictTypeDto> pageForm);

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    public List<DictTypeDto> selectDictTypeAll();

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    public DictTypeDto selectDictTypeById(String dictId);

    /**
     * 通过字典ID删除字典信息
     *
     * @param dictId 字典ID
     * @return 结果
     */
    public void deleteDictTypeById(String dictId);

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    public void deleteDictTypeByIds(List<String> dictIds);

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public DictTypeDto insertDictType(DictTypeDto dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    public String checkDictTypeUnique(DictTypeDto dict);

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public DictTypeDto updateDictType(DictTypeDto dictType);

    public void exportExcel(DictTypeDto dictType);

}
