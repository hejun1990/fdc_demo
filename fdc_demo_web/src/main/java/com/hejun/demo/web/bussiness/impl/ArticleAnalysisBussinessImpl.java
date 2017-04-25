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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
                        extractSohuWebContent(websiteSpider);
                    } else if (originalSiteCode == SpiderEntry.SINA.getCode()) {
                        extractSinaWebContent(websiteSpider);
                    } else if (originalSiteCode == SpiderEntry.QQ.getCode()) {
                        extractQQWebContent(websiteSpider);
                    }
                }
            } while (websiteSpiders != null && !websiteSpiders.isEmpty());
        } catch (Exception e) {
            logger.error("抓取网页正文报错：{}", e.toString());
        }
    }

    /**
     * 抓取搜狐科技文章
     *
     * @param websiteSpider
     * @throws Exception
     */
    private void extractSohuWebContent(WebsiteSpider websiteSpider) throws Exception {
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (html.contains("class=\"article\"")) {
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
            article.setKeywords(websiteSpider.getKeywords()); // 关键字
            articleContent.setSummary(websiteSpider.getSummary()); // 摘要

            Document doc = Jsoup.parse(html);
            doc.select("script,noscript,style,iframe,br").remove();
            // 抓取文章发布时间
            Element pubTimeEl = doc.select("span#news-time").first();
            if (pubTimeEl != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dataVal = pubTimeEl.attr("data-val");
                Date date = new Date(Long.parseLong(dataVal));
                String pubTime = formatter.format(date);
                article.setPubTime(date);
            }
            // 抓取文章正文
            Element content = doc.select("article.article").first();
            if (content != null) {
                String contentHtml = content.html();
                contentHtml = removeUselessAttribute(contentHtml);
                articleContent.setContent(contentHtml);
            }
            websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
        } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
            delWebsiteSpider(websiteSpider);
        }
    }

    /**
     * 抓取新浪科技文章
     *
     * @param websiteSpider
     * @throws Exception
     */
    private void extractSinaWebContent(WebsiteSpider websiteSpider) throws Exception {
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (html.contains("id=\"artibody\"")) {
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
            // 抓取文章关键字
            Element keywordsEl = doc.select("meta[name=keywords]").first();
            if (keywordsEl != null) {
                String keywords = keywordsEl.attr("content");
                String[] keywordsArr = keywords.split(",");
                StringBuilder keywordsBuilder = new StringBuilder();
                for (int i = 1; i < keywordsArr.length; i++) {
                    if (i != 1) {
                        keywordsBuilder.append(",");
                    }
                    keywordsBuilder.append(keywordsArr[i]);
                }
                article.setKeywords(keywordsBuilder.toString());
            }
            // 抓取文章发布时间
            Element pubTimeEl = doc.select("span.titer").first();
            if (pubTimeEl != null) {
                String pubTime = pubTimeEl.text();
                pubTime = pubTime.replace("年", "-").replace("月", "-").replace("日", "");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = formatter.parse(pubTime);
                article.setPubTime(date);
            }
            // 抓取文章正文
            Element content = doc.select("div#artibody").first();
            if (content != null) {
                // 去掉广告
                content.select("div.otherContent_01").remove();
                String contentHtml = content.html();
                contentHtml = removeUselessAttribute(contentHtml);
                articleContent.setContent(contentHtml);
            }
            websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
        } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
            delWebsiteSpider(websiteSpider);
        }
    }

    /**
     * 抓取腾讯科技文章
     *
     * @param websiteSpider
     * @throws Exception
     */
    private void extractQQWebContent(WebsiteSpider websiteSpider) throws Exception {
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (html.contains("id=\"Cnt-Main-Article-QQ\"")) {
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
            // 抓取文章关键字
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
            // 抓取文章发布时间
            Element pubTimeEl = doc.select("span.a_time").first();
            if (pubTimeEl != null) {
                String pubTime = pubTimeEl.text();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = formatter.parse(pubTime);
                article.setPubTime(date);
            }
            // 抓取文章正文
            Element content = doc.select("div#Cnt-Main-Article-QQ").first();
            if (content != null) {
                // 去掉视频
                content.select("div.rv-js-root").remove();
                String contentHtml = content.html();
                contentHtml = removeUselessAttribute(contentHtml);
                articleContent.setContent(contentHtml);
            }
            websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
        } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
            delWebsiteSpider(websiteSpider);
        }
    }

    /**
     * 去除元素中无用的属性
     *
     * @param contentHtml
     * @return
     */
    private String removeUselessAttribute(String contentHtml) {
        // 去除换行符
        contentHtml = contentHtml.replaceAll("[\\n\\r]", "");
        // 去掉元素的class属性
        contentHtml = contentHtml.replaceAll(" class=\"[\\w -]+\"", "");
        // 去掉元素的id属性
        contentHtml = contentHtml.replaceAll(" id=\"[\\w-]+\"", "");
        // 去掉注释
        contentHtml = contentHtml.replaceAll("<!--.*?-->", "");
        // 去掉元素的onmouseover属性
        contentHtml = contentHtml.replaceAll(" onmouseover=\".*?\"", "");
        // 去掉元素的onclick属性
        contentHtml = contentHtml.replaceAll(" onclick=\".*?\"", "");
        // 去掉元素的onmousedown属性
        contentHtml = contentHtml.replaceAll(" onmousedown=\".*?\"", "");
        // 去掉元素的onmousemove属性
        contentHtml = contentHtml.replaceAll(" onmousemove=\".*?\"", "");
        // 去掉元素的onmouseout属性
        contentHtml = contentHtml.replaceAll(" onmouseout=\".*?\"", "");
        // 去掉元素的onmouseover属性
        contentHtml = contentHtml.replaceAll(" onmouseover=\".*?\"", "");
        // 去掉元素的onmouseup属性
        contentHtml = contentHtml.replaceAll(" onmouseup=\".*?\"", "");
        return contentHtml;
    }

    /**
     * 删除爬取文章
     *
     * @param websiteSpider
     */
    private void delWebsiteSpider(WebsiteSpider websiteSpider) {
        WebsiteSpider record = new WebsiteSpider();
        WebsiteSpider example = new WebsiteSpider();
        String id = websiteSpider.getId();
        int version = websiteSpider.getVersion();
        int analysisCount = websiteSpider.getAnalysisCount();
        record.setAnalysisCount(analysisCount + 1);
        record.setVersion(version + 1);
        example.setId(id);
        example.setVersion(version);
        websiteSpiderService.deleteByConditionSelective(record, example);
    }

    public static void main(String[] args) {
//        String originalUrl = "http://www.pingwest.com/wp-admin/admin-ajax.php";
//        Map<String, Object> params = new HashMap<>();
//        params.put("action", "my_ajax_allpost_next");
//        params.put("paged", 3);
//        String html = HttpUtil.httpClientGet(originalUrl, params);
//        logger.info("品玩科技：\n{}", html);
        logger.info("品玩科技：\n{}", Math.random());
    }
}