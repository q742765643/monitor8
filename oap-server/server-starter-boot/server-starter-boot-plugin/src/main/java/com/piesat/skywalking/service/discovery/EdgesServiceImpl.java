package com.piesat.skywalking.service.discovery;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.skywalking.api.discover.EdgesService;
import com.piesat.skywalking.dao.EdgesDao;
import com.piesat.skywalking.dto.EdgesDto;
import com.piesat.skywalking.entity.EdgesEntity;
import com.piesat.skywalking.mapper.EdgesMapper;
import com.piesat.skywalking.mapstruct.EdgesMapstruct;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : EdgesServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2021-02-23 10:59
 */
@Service
public class EdgesServiceImpl extends BaseService<EdgesEntity> implements EdgesService {
    @Autowired
    private EdgesMapper edgesMapper;
    @Autowired
    private EdgesDao edgesDao;
    @Autowired
    private EdgesMapstruct edgesMapstruct;

    public List<String> selectBySource(String source){
        List<String> edgesEntities=edgesMapper.selectBySource(source);
        return edgesEntities;
    }
    public int deleteBySource(String source){
        return edgesMapper.deleteBySource(source);
    }
    public List<EdgesDto> selectAll(){
        List<EdgesEntity> edgesEntities=edgesDao.findAll();
        return edgesMapstruct.toDto(edgesEntities);
    }
    public void saveAll(List<EdgesDto> edgesDtos){
        edgesDao.saveAll(edgesMapstruct.toEntity(edgesDtos));
    }
    @Transactional
    public void updateTopy(String source,List<EdgesDto> edgesDtos){
         this.deleteBySource(source);
         this.saveAll(edgesDtos);
    }

    @Override
    public BaseDao<EdgesEntity> getBaseDao() {
        return edgesDao;
    }

    public List<EdgesDto> selectAllWithHost(){
        List<EdgesEntity> edgesEntities=edgesMapper.selectAllWithHost();
        return edgesMapstruct.toDto(edgesEntities);
    }
}

