package com.piesat.skywalking.api.discover;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.EdgesDto;
import com.piesat.util.constant.GrpcConstant;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface EdgesService {
    public List<String> selectBySource(String source);

    public int deleteBySource(String source);

    public List<EdgesDto> selectAll();

    public void saveAll(List<EdgesDto> edgesDtos);

    public void updateTopy(String source,List<EdgesDto> edgesDtos);

    public List<EdgesDto> selectAllWithHost();
}
