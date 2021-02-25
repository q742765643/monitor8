package com.piesat.skywalking.entity;

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

    @Column(name = "folder_regular", length = 255)
    private String folderRegular;

    @Column(name = "filename_regular", length = 255)
    private String filenameRegular;

    @Column(name = "file_sample", length = 255)
    private String fileSample;

    @Column(name = "file_num", length = 255)
    private Integer fileNum;

    @Column(name = "file_size", length = 255)
    private Integer fileSize;

    @Column(name = "scan_type", length = 1)
    private Integer scanType;

    @Column(name = "acount_id", length = 50)
    private String acountId;

    @Column(name = "delay_start", length = 50)
    private Integer delayStart;

    @Column(name = "delay_start_unit", length = 50)
    private String delayStartUnit;

    @Column(name = "range_time", length = 50)
    private Integer rangeTime;

    @Column(name = "range_unit", length = 50)
    private String rangeUnit;


}
