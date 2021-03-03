package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "T_MT_FILE_MONITOR")
@DiscriminatorValue("FILEMONITOR")
public class FileMonitorEntity extends HtJobInfo {

    @Excel(name = "资料文件目录规则")
    @Column(name = "folder_regular", length = 255)
    private String folderRegular;
    @Excel(name = "资料文件名规则")
    @Column(name = "filename_regular", length = 255)
    private String filenameRegular;
    @Excel(name = "文件路径样例")
    @Column(name = "file_sample", length = 255)
    private String fileSample;
    @Excel(name = "应到数量")
    @Column(name = "file_num", length = 255)
    private Integer fileNum;
    @Excel(name = "应到大小")
    @Column(name = "file_size", length = 255)
    private Integer fileSize;

    @Column(name = "scan_type", length = 1)
    private Integer scanType;

    @Column(name = "acount_id", length = 50)
    private String acountId;

    @Excel(name = "延迟启动")
    @Column(name = "delay_start", length = 50)
    private Integer delayStart;
    @Excel(name = "延迟启动单位",readConverterExp = "D=日,H=时,M=分,S=秒",combo = {"日","时","分","秒"})
    @Column(name = "delay_start_unit", length = 50)
    private String delayStartUnit;
    @Excel(name = "采集范围")
    @Column(name = "range_time", length = 50)
    private Integer rangeTime;
    @Excel(name = "采集范围单位",readConverterExp = "D=日,H=时,M=分,S=秒",combo = {"日","时","分","秒"})
    @Column(name = "range_unit", length = 50)
    private String rangeUnit;


}
