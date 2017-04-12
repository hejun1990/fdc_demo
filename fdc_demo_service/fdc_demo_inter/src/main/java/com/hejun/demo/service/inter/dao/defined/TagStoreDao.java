package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TagStoreDao {
    int countByCondition(Map<String, Object> condition);

    List<TagStore> selectPageByCondition(Map<String, Object> condition);
}