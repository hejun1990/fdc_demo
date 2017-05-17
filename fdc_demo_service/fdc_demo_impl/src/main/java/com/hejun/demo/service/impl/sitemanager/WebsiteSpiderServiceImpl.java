package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.defined.KeywordsStoreDao;
import com.hejun.demo.service.inter.dao.defined.WebsiteSpiderDao;
import com.hejun.demo.service.inter.dao.generation.*;
import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.*;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public class WebsiteSpiderServiceImpl implements WebsiteSpiderService {
    @Autowired
    private WebsiteSpiderMapper websiteSpiderMapper;

    @Autowired
    private WebsiteSpiderDao websiteSpiderDao;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Autowired
    private KeywordsStoreMapper keywordsStoreMapper;

    @Autowired
    private KeywordsStoreDao keywordsStoreDao;

    @Autowired
    private ArticleKeywordsMapper articleKeywordsMapper;

    @Override
    public int countByExample(WebsiteSpiderExample example) {
        return websiteSpiderMapper.countByExample(example);
    }

    @Override
    public boolean insertSelective(WebsiteSpider record) {
        return websiteSpiderMapper.insertSelective(record) > 0;
    }

    @Override
    public WebsiteSpider selectByPrimaryKey(String id) {
        return websiteSpiderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<WebsiteSpider> selectByExample(WebsiteSpiderExample example) {
        return websiteSpiderMapper.selectByExample(example);
    }

    @Override
    public boolean updateByPrimaryKeySelective(WebsiteSpider record) {
        return websiteSpiderMapper.updateByPrimaryKeySelective(record) > 0;
    }

    @Override
    public int countByCondition(Map<String, Object> record) {
        return websiteSpiderDao.countByCondition(record);
    }

    @Override
    public List<WebsiteSpider> selectPageByCondition(Map<String, Object> record, Paging paging) {
        return websiteSpiderDao.selectPageByCondition(record, paging);
    }

    @Override
    public boolean updateByConditionSelective(WebsiteSpider record, WebsiteSpider example) {
        return websiteSpiderDao.updateByConditionSelective(record, example) > 0;
    }

    @Override
    public boolean deleteByConditionSelective(WebsiteSpider record, WebsiteSpider example) {
        record.setIsDel(1);
        return websiteSpiderDao.updateByConditionSelective(record, example) > 0;
    }

    @Override
    public List<WebsiteSpider> selectPageByConditionNoOrder(Map<String, Object> record, Paging paging) {
        return websiteSpiderDao.selectPageByConditionNoOrder(record, paging);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean handleSpiderContent(WebsiteSpider websiteSpider, Article article, ArticleContent articleContent) {
        WebsiteSpider spiderRecord = new WebsiteSpider();
        WebsiteSpider spiderExample = new WebsiteSpider();
        String id = websiteSpider.getId();
        int version = websiteSpider.getVersion();
        int analysisCount = websiteSpider.getAnalysisCount();
        spiderRecord.setAnalysisCount(analysisCount + 1);
        spiderRecord.setVersion(version + 1);
        if (StringUtils.isEmpty(websiteSpider.getKeywords())) {
            if (StringUtils.isNotEmpty(article.getKeywords())) {
                spiderRecord.setKeywords(article.getKeywords());
            }
        }
        if (StringUtils.isEmpty(websiteSpider.getAuthor())) {
            if (StringUtils.isNotEmpty(article.getAuthor())) {
                spiderRecord.setAuthor(article.getAuthor());
            }
        }
        if (websiteSpider.getPubTime() == null) {
            if (article.getPubTime() != null) {
                spiderRecord.setPubTime(article.getPubTime());
            }
        }
        spiderExample.setId(id);
        spiderExample.setVersion(version);
        boolean result = this.updateByConditionSelective(spiderRecord, spiderExample);
        if (!result) {
            return false;
        }
        result = articleInsertSelective(article);
        if (!result) {
            return false;
        }
        articleContent.setArticleId(article.getId());
        result = articleContentInsertSelective(articleContent);
        if (!result) {
            return false;
        }
        String keywords = article.getKeywords();
        if (StringUtils.isNotEmpty(keywords)) {
            String[] keywordsArr = keywords.split(",");
            for (String word : keywordsArr) {
                Map<String, Object> keywordsRecord = new HashMap<>();
                keywordsRecord.put("keywords", word);
                List<KeywordsStore> keywordsStores = keywordsStoreDao.selectPageByCondition(keywordsRecord, null);
                // 如果关键字库中没有该关键字，则插入新关键字
                if (keywordsStores == null || keywordsStores.isEmpty()) {
                    KeywordsStore keywordsStore = new KeywordsStore();
                    keywordsStore.setKeywords(word);
                    result = keywordsStoreInsertSelective(keywordsStore);
                    if (!result) {
                        return false;
                    }
                    ArticleKeywords articleKeywords = new ArticleKeywords();
                    articleKeywords.setArticleId(article.getId());
                    articleKeywords.setKeywordsId(keywordsStore.getId());
                    result = articleKeywordsInsert(articleKeywords);
                    if (!result) {
                        return false;
                    }
                } else {
                    KeywordsStore keywordsStore = keywordsStores.get(0);
                    ArticleKeywords articleKeywords = new ArticleKeywords();
                    articleKeywords.setArticleId(article.getId());
                    articleKeywords.setKeywordsId(keywordsStore.getId());
                    result = articleKeywordsInsert(articleKeywords);
                    if (!result) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean articleInsertSelective(Article article) {
        return articleMapper.insertSelective(article) > 0;
    }

    private boolean articleContentInsertSelective(ArticleContent articleContent) {
        return articleContentMapper.insertSelective(articleContent) > 0;
    }

    private boolean keywordsStoreInsertSelective(KeywordsStore keywordsStore) {
        return keywordsStoreMapper.insertSelective(keywordsStore) > 0;
    }

    private boolean articleKeywordsInsert(ArticleKeywords articleKeywords) {
        return articleKeywordsMapper.insert(articleKeywords) > 0;
    }
}
