package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import lombok.Data;

@Data
public class HostConfigDto extends HtJobInfoDto {
    private String ip;
    private String hostName;
    private String type;
    private String isSnmp;
    private String isAgent;
    private String isSsh;
    private String os;
    private int sshPort;
    private String sshUserName;
    private String sshPassWord;
}
