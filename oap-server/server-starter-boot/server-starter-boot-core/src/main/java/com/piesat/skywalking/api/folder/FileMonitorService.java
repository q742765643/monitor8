package com.piesat.skywalking.api.folder;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.util.ResultT;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.io.InputStream;
import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface FileMonitorService {
    public PageBean selectPageList(PageForm<FileMonitorDto> pageForm);

    public List<FileMonitorDto> selectBySpecification(FileMonitorDto fileMonitorDto);

    public FileMonitorDto save(FileMonitorDto fileMonitorDto);

    public FileMonitorDto findById(String id);

    public void deleteByIds(List<String> ids);

    public long selectCount(FileMonitorDto fileMonitorDto);

    public boolean regularCheck(FileMonitorDto fileMonitorDto, ResultT<String> resultT);

    public FileMonitorDto updateFileMonitor(FileMonitorDto fileMonitorDto);

    public void trigger(String id);

    public List<FileMonitorDto> selectAll();

    public void exportExcel();

    public void uploadExcel(InputStream inputStream);

}
