package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public interface WebsiteSpiderService {
    int countByExample(WebsiteSpiderExample example);

    boolean insertSelective(WebsiteSpider record);

    WebsiteSpider selectByPrimaryKey(String id);

    List<WebsiteSpider> selectByExample(WebsiteSpiderExample example);

    boolean updateByPrimaryKeySelective(WebsiteSpider record);

    int countByCondition(Map<String, Object> record);

    List<WebsiteSpider> selectPageByCondition(Map<String, Object> record, Paging paging);

    boolean updateByConditionSelective(WebsiteSpider record, WebsiteSpider example);

    boolean deleteByConditionSelective(WebsiteSpider record, WebsiteSpider example);

    List<WebsiteSpider> selectPageByConditionNoOrder(Map<String, Object> record, Paging paging);

    /**
     * 处理爬取文章的正文
     *
     * @param websiteSpider
     * @param article
     * @param articleContent
     * @return
     */
    boolean handleSpiderContent(WebsiteSpider websiteSpider, Article article, ArticleContent articleContent);
}
