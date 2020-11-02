package com.piesat.skywalking.api.folder;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface DirectoryAccountService {
    public PageBean selectPageList(PageForm<DirectoryAccountDto> pageForm);

    public DirectoryAccountDto save(DirectoryAccountDto directoryAccountDto);

    public DirectoryAccountDto findById(String id);

    public List<DirectoryAccountDto> selectAll();

    public void deleteByIds(List<String> ids);
}
