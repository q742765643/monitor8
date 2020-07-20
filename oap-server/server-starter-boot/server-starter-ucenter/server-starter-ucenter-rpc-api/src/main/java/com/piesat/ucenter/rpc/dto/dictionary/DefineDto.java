package com.piesat.ucenter.rpc.dto.dictionary;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * @author yaya
 * @description TODO
 * @date 2019/12/26 17:17
 */
@Data
public class DefineDto extends BaseDto {
    /**
     * 区域标识
     */
    private String areaId;

    /**
     * 开始纬度
     */
    private Double startLat;

    /**
     * 结束纬度
     */
    private Double endLat;

    /**
     * 开始经度
     */
    private Double startLon;

    /**
     * 结束经度
     */
    private Double endLon;

    /**
     * 备注
     */
    private String areaDesc;
}
