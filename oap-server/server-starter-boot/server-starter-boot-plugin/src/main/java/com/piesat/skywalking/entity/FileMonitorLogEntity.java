package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName : FileMonitorLogEntity
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 17:41
 */
@Entity
@Data
@Table(name="T_MT_FILE_MONITOR_LOG")
public class FileMonitorLogEntity extends BaseEntity {
    @Column(name="task_id", length=50)
    private String taskId;

    @Column(name="folder_regular", length=255)
    private String folderRegular;

    @Column(name="filename_regular", length=255)
    private String filenameRegular;

    @Column(name="file_num", length=255)
    private long fileNum;

    @Column(name="file_size", length=255)
    private long fileSize;

    @Column(name="trigger_time")
    private long triggerTime;

    @Column(name="handle_code",length = 1)
    private Integer handleCode;

    @Column(name="handle_msg", columnDefinition = "TEXT")
    private String handleMsg;

    @Column(name="elapsed_time")
    private long elapsedTime;
}

