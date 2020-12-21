package com.piesat.skywalking.api.folder;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface FileMonitorLogService {
    public PageBean selectPageList(PageForm<FileMonitorLogDto> pageForm);

    public FileMonitorLogDto save(FileMonitorLogDto fileMonitorLogDto);

    public FileMonitorLogDto findById(String id);

    public void deleteByIds(List<String> ids);

    public PageBean selectPageListDetail(PageForm<FileMonitorLogDto> pageForm);
}
