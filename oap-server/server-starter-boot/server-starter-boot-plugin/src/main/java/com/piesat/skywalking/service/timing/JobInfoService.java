package com.piesat.skywalking.service.timing;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;


public interface JobInfoService {
    public PageBean selectPageList(PageForm<HtJobInfoDto> pageForm);

    public List<HtJobInfoDto> selectBySpecification(HtJobInfoDto htJobInfoDto);

    public HtJobInfoDto save(HtJobInfoDto htJobInfoDto);

    public HtJobInfoDto findById(String id);

    public void deleteByIds(List<String> ids);
}
