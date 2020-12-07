package com.piesat.skywalking.excel;

import com.piesat.common.annotation.Excel;
import lombok.Data;

/**
 * @ClassName : AlarmReportVo
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-04 15:04
 */
@Data
public class AlarmReportVo {
    @Excel(name = "类型")
    private String deviceType;
    @Excel(name = "一般")
    private long general;
    @Excel(name = "危险")
    private long danger;
    @Excel(name = "故障")
    private long fault;
    @Excel(name = "已处理")
    private long processed;
    @Excel(name = "未处理")
    private long unprocessed;

}

