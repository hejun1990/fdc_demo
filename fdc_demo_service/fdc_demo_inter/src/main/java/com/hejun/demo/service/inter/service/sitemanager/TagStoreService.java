package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public interface TagStoreService {
    boolean insertSelective(TagStore record);

    List<TagStore> selectByExample(TagStoreExample example);

    TagStore selectByPrimaryKey(String id);

    boolean updateByPrimaryKeySelective(@Param("record") TagStore record);

    int countByCondition(Map<String, Object> condition);

    List<TagStore> selectPageByCondition(Map<String, Object> condition);
}
