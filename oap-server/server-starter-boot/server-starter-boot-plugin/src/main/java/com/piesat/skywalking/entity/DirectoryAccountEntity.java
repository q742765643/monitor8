package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName : DirectoryAccountEntity
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-24 14:36
 */
@Entity
@Data
@Table(name = "T_MT_DIRECTORY_ACCOUNT")
public class DirectoryAccountEntity extends BaseEntity {
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "ip", length = 100)
    private String ip;

    @Column(name = "user", length = 100)
    private String user;

    @Column(name = "pass", length = 100)
    private String pass;
}

