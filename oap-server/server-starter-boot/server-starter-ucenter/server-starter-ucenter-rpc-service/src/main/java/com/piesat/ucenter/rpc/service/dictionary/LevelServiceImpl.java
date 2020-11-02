package com.piesat.ucenter.rpc.service.dictionary;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.ucenter.dao.dictionary.LevelDao;
import com.piesat.ucenter.entity.dictionary.LevelEntity;
import com.piesat.ucenter.mapper.dictionary.LevelMapper;
import com.piesat.ucenter.rpc.api.dictionary.LevelService;
import com.piesat.ucenter.rpc.dto.dictionary.LevelDto;
import com.piesat.ucenter.rpc.mapstruct.dictionary.LevelMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangyajuan
 * @description 层次属性管理
 * @date 2019年12月23日下午6:13:37
 */
@Service
public class LevelServiceImpl extends BaseService<LevelEntity> implements LevelService {
    @Autowired
    private LevelDao levelDao;
    @Autowired
    private LevelMapstruct levelMapstruct;
    @Autowired
    private LevelMapper levelMapper;

    @Override
    public BaseDao<LevelEntity> getBaseDao() {
        return levelDao;
    }

    @Override
    public PageBean selectLevelList(PageForm<LevelDto> pageForm) {
        LevelEntity levelEntity = levelMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(), pageForm.getPageSize());
        //根据条件查询当前分页所有
        List<LevelEntity> levelEntities = levelMapper.selectLevelList(levelEntity);
        PageInfo<LevelEntity> pageInfo = new PageInfo<>(levelEntities);
        //获取当前页数据
        List<LevelDto> levelDtos = levelMapstruct.toDto(pageInfo.getList());
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getPages(), levelDtos);
        return pageBean;
    }

    @Override
    public LevelDto saveDto(LevelDto levelDto) {
        LevelEntity levelEntity = this.levelMapstruct.toEntity(levelDto);
        List<LevelEntity> deptEntities = levelMapper.selectLevelList(levelEntity);
        if (deptEntities.isEmpty()) {
            levelEntity = this.saveNotNull(levelEntity);
        }
        return this.levelMapstruct.toDto(levelEntity);
    }

    @Override
    public void deleteLevelByIds(List<String> levelIds) {
        this.deleteByIds(levelIds);
    }

    @Override
    public LevelDto updateLevel(LevelDto levelDto) {
        LevelEntity levelEntity = this.levelMapstruct.toEntity(levelDto);
        levelEntity = this.saveNotNull(levelEntity);
        return this.levelMapstruct.toDto(levelEntity);
    }

    @Override
    public LevelDto getDotById(String id) {
        LevelEntity levelEntity = this.getById(id);
        return this.levelMapstruct.toDto(levelEntity);
    }

    @Override
    public List<LevelDto> getAllLevel() {
        List<LevelEntity> levelEntity = this.levelMapper.getAllLevel();
        return this.levelMapstruct.toDto(levelEntity);
    }
}
