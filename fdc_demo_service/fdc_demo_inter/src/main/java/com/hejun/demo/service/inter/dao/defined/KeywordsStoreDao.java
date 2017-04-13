package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordsStoreDao {
    int countByCondition(Map<String, Object> condition);

    List<KeywordsStore> selectPageByCondition(Map<String, Object> condition);
}