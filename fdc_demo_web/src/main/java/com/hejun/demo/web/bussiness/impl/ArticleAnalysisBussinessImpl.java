package com.hejun.demo.web.bussiness.impl;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.ArticleAnalysisBussiness;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.collections.map.HashedMap;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/17.
 */
@Service("articleAnalysisBussiness")
public class ArticleAnalysisBussinessImpl implements ArticleAnalysisBussiness {
    private static Logger logger = LoggerFactory.getLogger(ArticleAnalysisBussinessImpl.class);

    @Autowired
    private WebsiteSpiderService websiteSpiderService;

    @Override
    public void getWebContent() {
        /*
        Map<String, Object> condition = new HashMap<>();
        condition.put("page", 0);
        condition.put("size", 1);
        List<WebsiteSpider> websiteSpiders = websiteSpiderService.selectPageByCondition(condition);
        for(WebsiteSpider websiteSpider : websiteSpiders) {
            String originalUrl = websiteSpider.getOriginalUrl();
            try {
                String content = ContentExtractor.getContentByUrl(originalUrl);
                logger.info("网页：{}，正文：{}", originalUrl, content);
            } catch (Exception e) {
                logger.error("抓取网页正文报错：{}", e.toString());
            }
        }
        */
        try {
            String originalUrl = "http://www.sohu.com/a/134652919_245605?loc=1&focus_pic=0";
            String html = HttpUtil.httpClientGet(originalUrl);
            Element contentElement = ContentExtractor.getContentElementByHtml(html, originalUrl);
            logger.info("正文标签：{}", contentElement.html());
        } catch (Exception e) {
            logger.error("抓取网页正文报错：{}", e.toString());
        }
    }
}
