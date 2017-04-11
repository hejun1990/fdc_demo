package com.hejun.demo.service.inter.dao.generation;

import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagStoreMapper {
    int countByExample(TagStoreExample example);

    int deleteByExample(TagStoreExample example);

    int deleteByPrimaryKey(String id);

    int insert(TagStore record);

    int insertSelective(TagStore record);

    List<TagStore> selectByExample(TagStoreExample example);

    TagStore selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TagStore record, @Param("example") TagStoreExample example);

    int updateByExample(@Param("record") TagStore record, @Param("example") TagStoreExample example);

    int updateByPrimaryKeySelective(@Param("record") TagStore record);

    int updateByPrimaryKey(TagStore record);
}