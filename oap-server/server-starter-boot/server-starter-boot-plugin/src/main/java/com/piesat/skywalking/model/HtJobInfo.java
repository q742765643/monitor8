package com.piesat.skywalking.model;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "T_MT_JOB_INFO")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "type")
public class HtJobInfo extends BaseEntity {
    @Column(name = "trigger_last_time", length = 50)
    private long triggerLastTime;    // 上次调度时间
    @Column(name = "trigger_next_time", length = 50)
    private long triggerNextTime;
    @Column(name = "trigger_status", length = 50)
    private Integer triggerStatus;
    @Column(name = "job_cron", length = 255)
    private String jobCron;
    @Excel(name = "任务名称")
    @Column(name = "task_name", length = 255)
    private String taskName;
    @Excel(name = "任务描述")
    @Column(name = "job_desc", length = 255)
    private String jobDesc;
    @Column(name = "job_handler", length = 255)
    private String jobHandler;
    @Column(name = "trigger_type", length = 10)
    private Integer triggerType;

    @Excel(name ="时区",readConverterExp = "0=北京时,1=世界时",combo = {"北京时","世界时"})
    @Column(name = "is_ut", length = 10)
    private Integer isUt;
    @Column(name = "delay_time", length = 50)
    private long delayTime;
    @Column(name = "is_alarm", length = 50)
    private Integer isAlarm;

    @Column(name = "address", length = 255)
    private String address;
}
