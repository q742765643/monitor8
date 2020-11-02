package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 17:57
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface DictDataService {
    /**
     * 根据条件分页查询字典数据
     *
     * @param pageForm 字典数据信息
     * @return 字典数据集合信息
     */
    public PageBean selectDictDataList(PageForm<DictDataDto> pageForm);


    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    public DictDataDto selectDictDataById(String dictCode);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    public List<DictDataDto> selectDictDataByType(String dictType);

    /**
     * 通过字典ID删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    public void deleteDictDataById(String dictCode);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    public void deleteDictDataByIds(List<String> dictCodes);

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public DictDataDto insertDictData(DictDataDto dictData);


    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    public DictDataDto updateDictData(DictDataDto dictData);

    /**
     * @param dictType
     * @param dictValue
     * @return
     */
    DictDataDto findByDictTypeAndDictValue(String dictType, String dictValue);

    public void exportExcel(DictDataDto dictData);
}
