package com.hejun.demo.service.inter.dao.generation;

import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.ArticleContentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleContentMapper {
    int countByExample(ArticleContentExample example);

    int deleteByExample(ArticleContentExample example);

    int deleteByPrimaryKey(String id);

    int insert(ArticleContent record);

    int insertSelective(ArticleContent record);

    List<ArticleContent> selectByExampleWithBLOBs(ArticleContentExample example);

    List<ArticleContent> selectByExample(ArticleContentExample example);

    ArticleContent selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ArticleContent record, @Param("example") ArticleContentExample example);

    int updateByExampleWithBLOBs(@Param("record") ArticleContent record, @Param("example") ArticleContentExample example);

    int updateByExample(@Param("record") ArticleContent record, @Param("example") ArticleContentExample example);

    int updateByPrimaryKeySelective(@Param("record") ArticleContent record);

    int updateByPrimaryKeyWithBLOBs(ArticleContent record);

    int updateByPrimaryKey(ArticleContent record);
}