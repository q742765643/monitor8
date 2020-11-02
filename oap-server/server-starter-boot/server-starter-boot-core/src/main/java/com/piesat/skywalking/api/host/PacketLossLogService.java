package com.piesat.skywalking.api.host;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.PacketLossLogDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface PacketLossLogService {
    public PageBean selectPageList(PageForm<PacketLossLogDto> pageForm);

    public List<PacketLossLogDto> selectBySpecification(PacketLossLogDto packetLossLogDto);

    public PacketLossLogDto save(PacketLossLogDto packetLossLogDto);

    public List<PacketLossLogDto> selectAll();

    public PacketLossLogDto findById(String id);

    public void deleteByIds(List<String> ids);
}
