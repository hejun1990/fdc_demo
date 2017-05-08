package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleExample;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import org.apache.ibatis.annotations.Param;

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

    int countByCondition(@Param("record") Map<String, Object> record);

    List<Article> selectPageByCondition(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);

    boolean updateByConditionSelective(@Param("record") Article record, @Param("example") Article example);

    List<Article> selectPageByConditionNoOrder(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);

    boolean repairWebArticlePubtime(Article article);
}
