package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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
@Table(name = "T_MT_FILE_MONITOR_LOG")
public class FileMonitorLogEntity extends BaseEntity {
    @Column(name = "task_id", length = 50)
    private String taskId;

    @Column(name = "remote_path", length = 255)
    private String remotePath;

    @Column(name = "folder_regular", length = 255)
    private String folderRegular;

    @Column(name = "filename_regular", length = 255)
    private String filenameRegular;

    @Column(name = "file_num", length = 255)
    private long fileNum;

    @Column(name = "file_size", length = 255)
    private long fileSize;

    @Column(name = "real_file_num", length = 255)
    private long realFileNum;

    @Column(name = "real_file_size", length = 255)
    private long realFileSize;

    @Column(name = "late_num", length = 255)
    private long lateNum;

    @Column(name = "trigger_time")
    private long triggerTime;

    @Column(name = "handle_code", length = 1)
    private Integer handleCode;

    @Column(name = "handle_msg", columnDefinition = "TEXT")
    private String handleMsg;

    @Column(name = "elapsed_time", length = 100)
    private long elapsedTime;

    @Column(name = "expression", length = 100)
    private String expression;
    @Column(name = "is_ut", length = 1)
    private Integer isUt;
    @Column(name = "job_cron", length = 100)
    private String jobCron;
    @Column(name = "task_name", length = 255)
    private String taskName;
    @Column(name = "is_compensation", length = 1)
    private Integer isCompensation;
}

