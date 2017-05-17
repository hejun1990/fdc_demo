package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleExample;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public interface ArticleService {
    int countByExample(ArticleExample example);

    boolean insertSelective(Article record);

    List<Article> selectByExample(ArticleExample example);

    Article selectByPrimaryKey(String id);

    boolean updateByPrimaryKeySelective(Article record);

    int countByCondition(Map<String, Object> record);

    List<Article> selectPageByCondition(Map<String, Object> record, Paging paging);

    boolean updateByConditionSelective(Article record, Article example);

    List<Article> selectPageByConditionNoOrder(Map<String, Object> record, Paging paging);

    boolean repairWebArticlePubtime(Article article);
}
