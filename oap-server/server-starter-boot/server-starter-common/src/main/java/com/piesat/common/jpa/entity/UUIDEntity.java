package com.piesat.common.jpa.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-11-17 18:18
 **/
@Data
@MappedSuperclass
public class UUIDEntity implements Serializable {
    @Id
    @Column(length = 50)
    @GeneratedValue(generator = "htht.uuid")
    @GenericGenerator(name = "htht.uuid", strategy = "com.piesat.common.jpa.generator.UUIDStringGenerator")
    private String id;

}

