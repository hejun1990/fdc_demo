package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.defined.KeywordsStoreDao;
import com.hejun.demo.service.inter.dao.generation.KeywordsStoreMapper;
import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import com.hejun.demo.service.inter.service.sitemanager.KeywordsStoreService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/13.
 */
public class KeywordsStoreServiceImpl implements KeywordsStoreService {
    @Autowired
    private KeywordsStoreMapper keywordsStoreMapper;
    @Autowired
    private KeywordsStoreDao keywordsStoreDao;

    @Override
    public boolean insertSelective(KeywordsStore record) {
        return keywordsStoreMapper.insertSelective(record) > 0;
    }

    @Override
    public KeywordsStore selectByPrimaryKey(String id) {
        return keywordsStoreMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKeySelective(@Param("record") KeywordsStore record) {
        return keywordsStoreMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public int countByCondition(Map<String, Object> condition) {
        return keywordsStoreDao.countByCondition(condition);
    }

    @Override
    public List<KeywordsStore> selectPageByCondition(Map<String, Object> record, Paging paging) {
        return keywordsStoreDao.selectPageByCondition(record, paging);
    }
}
