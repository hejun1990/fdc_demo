package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.ArticleContentExample;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public interface ArticleContentService {
    int countByExample(ArticleContentExample example);

    int insertSelective(ArticleContent record);

    List<ArticleContent> selectByExampleWithBLOBs(ArticleContentExample example);

    List<ArticleContent> selectByExample(ArticleContentExample example);

    ArticleContent selectByPrimaryKey(String id);

    int updateByExampleSelective(ArticleContent record, ArticleContentExample example);

    int updateByPrimaryKeySelective(ArticleContent record);
}
