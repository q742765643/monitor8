package com.piesat.skywalking.api.folder;

import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

public interface DirectoryAccountService {
    public PageBean selectPageList(PageForm<DirectoryAccountDto> pageForm);

    public DirectoryAccountDto save(DirectoryAccountDto directoryAccountDto);

    public DirectoryAccountDto findById(String id);


    public void deleteByIds(List<String> ids);
}
