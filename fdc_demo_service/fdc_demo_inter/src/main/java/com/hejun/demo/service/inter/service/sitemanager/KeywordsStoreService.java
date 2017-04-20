package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.KeywordsStore;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/13.
 */
public interface KeywordsStoreService {
    boolean insertSelective(KeywordsStore record);

    KeywordsStore selectByPrimaryKey(String id);

    boolean updateByPrimaryKeySelective(KeywordsStore record);

    int countByCondition(Map<String, Object> record);

    List<KeywordsStore> selectPageByCondition(Map<String, Object> record, Paging paging);
}
