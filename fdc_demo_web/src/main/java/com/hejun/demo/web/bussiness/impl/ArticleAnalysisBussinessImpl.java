package com.hejun.demo.web.bussiness.impl;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.ArticleAnalysisBussiness;
import com.hejun.demo.web.enumeration.ArticleType;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hejun-FDC on 2017/4/17.
 */
@Service("articleAnalysisBussiness")
public class ArticleAnalysisBussinessImpl implements ArticleAnalysisBussiness {
    private static Logger logger = LoggerFactory.getLogger(ArticleAnalysisBussinessImpl.class);

    @Autowired
    private WebsiteSpiderService websiteSpiderService;

    @Override
    public void extractWebContent() {
        try {
            Map<String, Object> record = new HashMap<>();
            record.put("analysisCount", 0);
            Paging paging = new Paging();
            List<WebsiteSpider> websiteSpiders = websiteSpiderService.selectPageByCondition(record, paging);
            do {
                for (WebsiteSpider websiteSpider : websiteSpiders) {
                    int originalSiteCode = websiteSpider.getOriginalSiteCode();
                    if (originalSiteCode == SpiderEntry.SOHU.getCode()) {

                    } else if (originalSiteCode == SpiderEntry.SINA.getCode()) {

                    } else if (originalSiteCode == SpiderEntry.QQ.getCode()) {
                        extractQQWebContent(websiteSpider);
                    }
                }
                websiteSpiders = websiteSpiderService.selectPageByCondition(record, paging);
            } while (websiteSpiders != null && !websiteSpiders.isEmpty());
        } catch (Exception e) {
            logger.error("抓取网页正文报错：{}", e.toString());
        }
    }

    private void extractQQWebContent(WebsiteSpider websiteSpider) throws Exception {
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (html.contains("Cnt-Main-Article-QQ")) {
            Article article = new Article();
            ArticleContent articleContent = new ArticleContent();

            article.setArticleType(ArticleType.TECHNOLOGY.getCode()); // 文章类型
            article.setTitle(websiteSpider.getTitle()); // 标题
            article.setViceTitle(websiteSpider.getViceTitle()); // 副标题(短标题)
            article.setAuthor(websiteSpider.getAuthor()); // 作者
            article.setOriginalSiteCode(websiteSpider.getOriginalSiteCode()); // 来源网站编号
            article.setOriginalSiteName(websiteSpider.getOriginalSiteName()); // 来源网站名称
            article.setOriginalUrl(websiteSpider.getOriginalUrl()); // 来源链接
            article.setPicUrl(websiteSpider.getPicUrl()); // 图片
            articleContent.setSummary(websiteSpider.getSummary()); // 摘要

            Document doc = Jsoup.parse(html);
            doc.select("script,noscript,style,iframe,br").remove();
            Element keywordsEl = doc.select("meta[name=keywords]").first();
            if (keywordsEl != null) {
                String keywords = keywordsEl.attr("content");
                String[] keywordsArr = keywords.split(",");
                if (keywordsArr.length > 1) {
                    StringBuilder keywordsBuilder = new StringBuilder();
                    for (int i = 1; i < keywordsArr.length; i++) {
                        if (i != 1) {
                            keywordsBuilder.append(",");
                        }
                        keywordsBuilder.append(keywordsArr[i]);
                    }
                    article.setKeywords(keywordsBuilder.toString());
                }
            }
            Element pubTimeEl = doc.select("span.a_time").first();
            if (pubTimeEl != null) {
                String pubTime = pubTimeEl.text();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = formatter.parse(pubTime);
                article.setPubTime(date);
            }
            Element content = doc.select("div#Cnt-Main-Article-QQ").first();
            if (content != null) {
                // 去掉视频
                content.select("div.rv-js-root").remove();
                String contentHtml = content.html();
                // 去除换行符
                contentHtml = contentHtml.replaceAll("[\\n\\r]", "");
                // 去掉元素的class样式
                contentHtml = contentHtml.replaceAll(" class=\"[\\w-]+\"", "");
                // 去掉元素的id
                contentHtml = contentHtml.replaceAll(" id=\"[\\w-]+\"", "");
                // 去掉注释
                contentHtml = contentHtml.replaceAll("<!--.*?-->", "");
                articleContent.setContent(contentHtml);
            }
            websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
        } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
            WebsiteSpider record = new WebsiteSpider();
            WebsiteSpider example = new WebsiteSpider();
            String id = websiteSpider.getId();
            int version = websiteSpider.getVersion();
            int analysisCount = websiteSpider.getAnalysisCount();
            record.setAnalysisCount(analysisCount + 1);
            record.setVersion(version + 1);
            example.setId(id);
            example.setVersion(version);
            // 删除该爬取文章
            websiteSpiderService.deleteByConditionSelective(record, example);
        }
    }

    @Override
    public void testContentHtml() {
        /*  腾讯测试
        String qq_originalUrl = "http://tech.qq.com/a/20170421/012711.htm";
        String html = HttpUtil.httpClientGet(qq_originalUrl);
        if (html.contains("Cnt-Main-Article-QQ")) {
            Document doc = Jsoup.parse(html);
            doc.select("script,noscript,style,iframe,br").remove();
            Element content = doc.select("div#Cnt-Main-Article-QQ").first();
            if (content != null) {
                // 去掉视频
                content.select("div.rv-js-root").remove();
                String contentHtml = content.html();
                contentHtml = contentHtml.replaceAll("[\\n\\r]", "");
                logger.info("腾讯科技正文处理前：\n{}", contentHtml);
                contentHtml = contentHtml.replaceAll(" class=\"[\\w-]+\"", "");
                contentHtml = contentHtml.replaceAll(" id=\"[\\w-]+\"", "");
                contentHtml = contentHtml.replaceAll("<!--.*?-->", "");
                logger.info("腾讯科技正文处理后：\n{}", contentHtml);
            }
        }
        */
        String sina_originalUrl = "http://tech.sina.com.cn/i/2017-03-21/doc-ifycnpiu9254392.shtml";
        String html = HttpUtil.httpClientGet(sina_originalUrl);
        if (html.contains("id=\"artibody\"")) {
            Document doc = Jsoup.parse(html);
            doc.select("script,noscript,style,iframe,br").remove();
            Element content = doc.select("div#artibody").first();
            if (content != null) {
                // 去掉广告
                
            }
        }
    }
}