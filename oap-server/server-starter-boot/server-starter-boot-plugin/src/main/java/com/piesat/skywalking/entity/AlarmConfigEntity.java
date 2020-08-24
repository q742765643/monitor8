package com.piesat.skywalking.entity;

import com.piesat.skywalking.dto.ConditionDto;
import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name="T_MT_ALARM_CONFIG")
@DiscriminatorValue("ALARM")
public class AlarmConfigEntity extends HtJobInfo {

    @Column(name="monitor_type", length=100)
    private Integer monitorType;

    @Transient
    private List<ConditionDto> generals;

    @Transient
    private List<ConditionDto> dangers;

    @Transient
    private List<ConditionDto> severitys;

    @Column(name="general", columnDefinition = "TEXT")
    private String general;

    @Column(name="danger", columnDefinition = "TEXT")
    private String danger;

    @Column(name="severity", columnDefinition = "TEXT")
    private String severity;
}
