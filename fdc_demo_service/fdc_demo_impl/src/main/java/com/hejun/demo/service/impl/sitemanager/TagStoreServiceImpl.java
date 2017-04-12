package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.defined.TagStoreDao;
import com.hejun.demo.service.inter.dao.generation.TagStoreMapper;
import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;
import com.hejun.demo.service.inter.service.sitemanager.TagStoreService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public class TagStoreServiceImpl implements TagStoreService {
    @Autowired
    private TagStoreMapper tagStoreMapper;

    @Autowired
    private TagStoreDao tagStoreDao;

    @Override
    public boolean insertSelective(TagStore record) {
        return tagStoreMapper.insertSelective(record) > 0;
    }

    @Override
    public List<TagStore> selectByExample(TagStoreExample example) {
        return tagStoreMapper.selectByExample(example);
    }

    @Override
    public TagStore selectByPrimaryKey(String id) {
        return tagStoreMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKeySelective(@Param("record") TagStore record) {
        return tagStoreMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public int countByCondition(Map<String, Object> condition) {
        return tagStoreDao.countByCondition(condition);
    }

    @Override
    public List<TagStore> selectPageByCondition(Map<String, Object> condition) {
        return tagStoreDao.selectPageByCondition(condition);
    }
}