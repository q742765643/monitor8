package com.piesat.skywalking.schedule.service;

import com.piesat.skywalking.api.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public String getTest(String aa) {
        System.out.println(aa);
        return "sssssssssssss";
    }
}
