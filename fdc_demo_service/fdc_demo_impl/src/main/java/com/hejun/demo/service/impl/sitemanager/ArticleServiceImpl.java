package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.defined.ArticleDao;
import com.hejun.demo.service.inter.dao.defined.WebsiteSpiderDao;
import com.hejun.demo.service.inter.dao.generation.ArticleMapper;
import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleExample;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.service.sitemanager.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private WebsiteSpiderDao websiteSpiderDao;

    @Override
    public int countByExample(ArticleExample example) {
        return articleMapper.countByExample(example);
    }

    @Override
    public boolean insertSelective(Article record) {
        return articleMapper.insertSelective(record) > 0;
    }

    @Override
    public List<Article> selectByExample(ArticleExample example) {
        return articleMapper.selectByExample(example);
    }

    @Override
    public Article selectByPrimaryKey(String id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKeySelective(Article record) {
        return articleMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public int countByCondition(Map<String, Object> record) {
        return articleDao.countByCondition(record);
    }

    @Override
    public List<Article> selectPageByCondition(Map<String, Object> record, Paging paging) {
        return articleDao.selectPageByCondition(record, paging);
    }

    @Override
    public boolean updateByConditionSelective(Article record, Article example) {
        return articleDao.updateByConditionSelective(record, example) > 0;
    }

    @Override
    public List<Article> selectPageByConditionNoOrder(Map<String, Object> record, Paging paging) {
        return articleDao.selectPageByConditionNoOrder(record, paging);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean repairWebArticlePubtime(Article article) {
        Article articleRecord = new Article();
        Article articleExample = new Article();
        articleRecord.setPubTime(article.getPubTime());
        articleRecord.setVersion(article.getVersion() + 1);
        articleExample.setId(article.getId());
        articleExample.setVersion(article.getVersion());
        boolean result = this.updateByConditionSelective(articleRecord, articleExample);
        if (!result) {
            return false;
        }

        WebsiteSpider spiderRecord = new WebsiteSpider();
        WebsiteSpider spiderExample = new WebsiteSpider();
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("originalUrl", article.getOriginalUrl());
        List<WebsiteSpider> websiteSpiders = websiteSpiderDao.selectPageByConditionNoOrder(recordMap, null);
        if (websiteSpiders != null && !websiteSpiders.isEmpty()) {
            WebsiteSpider websiteSpider = websiteSpiders.get(0);
            spiderRecord.setPubTime(article.getPubTime());
            spiderRecord.setVersion(websiteSpider.getVersion() + 1);
            spiderRecord.setAnalysisCount(websiteSpider.getAnalysisCount() + 1);
            spiderExample.setId(websiteSpider.getId());
            spiderExample.setVersion(websiteSpider.getVersion());
            result = websiteSpiderDao.updateByConditionSelective(spiderRecord, spiderExample) > 0;
            if (!result) {
                return false;
            }
        }
        return true;
    }
}
