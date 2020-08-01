package com.piesat.skywalking.api.alarm;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface AlarmConfigService {
    public PageBean selectPageList(PageForm<AlarmConfigDto> pageForm);

    public List<AlarmConfigDto> selectBySpecification(AlarmConfigDto alarmConfigDto);

    public AlarmConfigDto save(AlarmConfigDto alarmConfigDto);

    public AlarmConfigDto findById(String id);

    public void deleteByIds(List<String> ids);
}
