package com.piesat.skywalking.mapper;

import com.piesat.skywalking.model.HtJobInfo;
import com.piesat.ucenter.entity.dictionary.DefineEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JobInfoMapper {
    public List<HtJobInfo> selectList(HtJobInfo htJobInfo);
}
