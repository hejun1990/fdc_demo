package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import com.hejun.demo.service.inter.domain.generation.KeywordsStoreExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/13.
 */
public interface KeywordsStoreService {
    boolean insertSelective(KeywordsStore record);

    KeywordsStore selectByPrimaryKey(String id);

    boolean updateByPrimaryKeySelective(@Param("record") KeywordsStore record);

    int countByCondition(Map<String, Object> condition);

    List<KeywordsStore> selectPageByCondition(Map<String, Object> condition);
}
