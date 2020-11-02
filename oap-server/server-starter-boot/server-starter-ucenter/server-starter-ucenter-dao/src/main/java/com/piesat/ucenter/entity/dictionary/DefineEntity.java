package com.piesat.ucenter.entity.dictionary;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yaya
 * @description 区域类别实体类
 * @date 2019/12/26 16:55
 */
@Data
@Entity
@Table(name = "T_SOD_GRID_AREA_DEFINE")
public class DefineEntity extends BaseEntity {

    /**
     * 区域标识
     */
    @Excel(name = "区域标识")
    @Column(name = "area_id", length = 10)
    private String areaId;

    /**
     * 开始纬度
     */
    @Excel(name = "开始纬度")
    @Column(name = "start_lat")
    private Double startLat;

    /**
     * 结束纬度
     */
    @Excel(name = "结束纬度")
    @Column(name = "end_lat")
    private Double endLat;

    /**
     * 开始经度
     */
    @Excel(name = "开始经度")
    @Column(name = "start_lon")
    private Double startLon;

    /**
     * 结束经度
     */
    @Excel(name = "结束经度")
    @Column(name = "end_lon")
    private Double endLon;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @Column(name = "area_desc", length = 30)
    private String areaDesc;

}
