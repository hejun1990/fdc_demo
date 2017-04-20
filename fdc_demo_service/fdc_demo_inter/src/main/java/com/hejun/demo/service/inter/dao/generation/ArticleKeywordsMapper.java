package com.hejun.demo.service.inter.dao.generation;

import com.hejun.demo.service.inter.domain.generation.ArticleKeywords;
import com.hejun.demo.service.inter.domain.generation.ArticleKeywordsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleKeywordsMapper {
    int countByExample(ArticleKeywordsExample example);

    int deleteByExample(ArticleKeywordsExample example);

    int insert(ArticleKeywords record);

    int insertSelective(ArticleKeywords record);

    List<ArticleKeywords> selectByExample(ArticleKeywordsExample example);

    int updateByExampleSelective(@Param("record") ArticleKeywords record, @Param("example") ArticleKeywordsExample example);

    int updateByExample(@Param("record") ArticleKeywords record, @Param("example") ArticleKeywordsExample example);
}