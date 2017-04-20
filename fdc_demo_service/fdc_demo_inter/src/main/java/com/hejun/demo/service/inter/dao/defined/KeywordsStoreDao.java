package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordsStoreDao {
    int countByCondition(@Param("record") Map<String, Object> record);

    List<KeywordsStore> selectPageByCondition(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);
}