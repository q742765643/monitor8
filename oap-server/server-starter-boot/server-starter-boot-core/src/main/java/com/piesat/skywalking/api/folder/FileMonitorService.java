package com.piesat.skywalking.api.folder;

import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

public interface FileMonitorService {
    public PageBean selectPageList(PageForm<FileMonitorDto> pageForm);

    public List<FileMonitorDto> selectBySpecification(FileMonitorDto fileMonitorDto);

    public FileMonitorDto save(FileMonitorDto fileMonitorDto);

    public FileMonitorDto findById(String id);

    public void deleteByIds(List<String> ids);

}
