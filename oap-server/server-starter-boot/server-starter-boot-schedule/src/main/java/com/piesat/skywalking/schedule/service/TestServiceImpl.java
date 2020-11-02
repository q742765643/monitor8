package com.piesat.skywalking.schedule.service;

import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.skywalking.api.TestService;
import com.piesat.skywalking.schedule.service.folder.FileLocalService;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public String getTest(String aa) {
        System.out.println(aa);
        FileBaseService fileBaseService = SpringUtil.getBean(FileLocalService.class);
        return "sssssssssssss";
    }
}
