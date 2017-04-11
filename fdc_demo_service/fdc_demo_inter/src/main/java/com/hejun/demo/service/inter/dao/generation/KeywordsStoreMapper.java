package com.hejun.demo.service.inter.dao.generation;

import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import com.hejun.demo.service.inter.domain.generation.KeywordsStoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordsStoreMapper {
    int countByExample(KeywordsStoreExample example);

    int deleteByExample(KeywordsStoreExample example);

    int deleteByPrimaryKey(String id);

    int insert(KeywordsStore record);

    int insertSelective(KeywordsStore record);

    List<KeywordsStore> selectByExample(KeywordsStoreExample example);

    KeywordsStore selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") KeywordsStore record, @Param("example") KeywordsStoreExample example);

    int updateByExample(@Param("record") KeywordsStore record, @Param("example") KeywordsStoreExample example);

    int updateByPrimaryKeySelective(@Param("record") KeywordsStore record);

    int updateByPrimaryKey(KeywordsStore record);
}