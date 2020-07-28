package com.piesat.skywalking.entity;

import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="T_MT_FILE_MONITOR")
@DiscriminatorValue("FILEMONITOR")
public class FileMonitorEntity extends HtJobInfo {

    @Column(name="folder_regular", length=255)
    private String floderRegular;

    @Column(name="filename_regular", length=255)
    private String filenameRegular;

    @Column(name="file_sample", length=255)
    private String fileSample;

    @Column(name="file_num", length=255)
    private long fileNum;

    @Column(name="file_size", length=255)
    private long fileSize;

}
