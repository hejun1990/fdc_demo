package com.hejun.demo.web.bussiness.impl;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.service.sitemanager.ArticleService;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.ArticleAnalysisBussiness;
import com.hejun.demo.web.enumeration.ArticleType;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejun-FDC on 2017/4/17.
 */
@Service("articleAnalysisBussiness")
public class ArticleAnalysisBussinessImpl implements ArticleAnalysisBussiness {
    private static Logger logger = LoggerFactory.getLogger(ArticleAnalysisBussinessImpl.class);

    @Autowired
    private WebsiteSpiderService websiteSpiderService;

    @Autowired
    private ArticleService articleService;

    @Override
    public void extractWebContent(int begin) {
        // 已经分析的文章数量
        int currentChapter = 1;
        // 当前线程名称
        String currentThreadName = Thread.currentThread().getName();
        long startTime = System.currentTimeMillis();
        Map<String, Object> record = new HashMap<>();
        record.put("analysisCount", 0);
        record.put("isDel", 0);
//        record.put("originalUrl", "http://tech.163.com/15/1127/05/B9DE859T000915BF.html");
        Paging paging = new Paging(begin);
        List<WebsiteSpider> websiteSpiders = websiteSpiderService.selectPageByConditionNoOrder(record, paging);
        do {
            for (WebsiteSpider websiteSpider : websiteSpiders) {
                int originalSiteCode = websiteSpider.getOriginalSiteCode();
                if (originalSiteCode == SpiderEntry.SOHU.getCode()) {
                    extractSohuWebContent(websiteSpider, currentThreadName, currentChapter);
                } else if (originalSiteCode == SpiderEntry.SINA.getCode()) {
                    extractSinaWebContent(websiteSpider, currentThreadName, currentChapter);
                } else if (originalSiteCode == SpiderEntry.QQ.getCode()) {
                    extractQQWebContent(websiteSpider, currentThreadName, currentChapter);
                } else if (originalSiteCode == SpiderEntry.WANGYI.getCode()) {
                    extractWangyiWebContent(websiteSpider, currentThreadName, currentChapter);
                }
                // 处理完一片文章
                currentChapter++;
            }
            // 处理完10篇文章，获取新的未处理的10篇文章
            websiteSpiders = websiteSpiderService.selectPageByConditionNoOrder(record, paging);
        } while (websiteSpiders != null && !websiteSpiders.isEmpty());
        long endTime = System.currentTimeMillis();
        Object[] logVals = {currentThreadName, (endTime - startTime) / (1000 * 60), ((endTime - startTime) % (1000 * 60)) / 1000};
        logger.info("[{}]结束处理文章任务，耗时{}分{}秒。", logVals);
    }

    /**
     * 抓取搜狐科技文章
     *
     * @param websiteSpider
     * @param currentThreadName
     * @param currentChapter
     */
    private void extractSohuWebContent(WebsiteSpider websiteSpider, String currentThreadName, int currentChapter) {
        boolean handleResult;
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (StringUtils.isNotEmpty(html)) {
            if (html.contains("class=\"article\"")) {
                try {
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
                        String dataVal = pubTimeEl.attr("data-val");
                        Date date = new Date(Long.parseLong(dataVal));
                        article.setPubTime(date);
                    }
                    // 抓取文章正文
                    Element content = doc.select("article.article").first();
                    if (content != null) {
                        String contentHtml = content.html();
                        contentHtml = removeUselessAttribute(contentHtml);
                        articleContent.setContent(contentHtml);
                    }
                    handleResult = websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            handleResult ? "处理成功" : "已经被处理过"};
                    logger.info("[{}]处理第{}篇文章，来源搜狐科技，标题\"{}\"，结果：{}", logVals);
                } catch (Exception e) {
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            e.getMessage(), websiteSpider.getOriginalUrl()};
                    logger.error("[{}]处理第{}篇文章，来源搜狐科技，标题\"{}\"，结果异常：{}, 网址：{}", logVals);
                }
            } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
                handleResult = delWebsiteSpider(websiteSpider);
                Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                        handleResult ? "删除成功" : "删除失败"};
                logger.warn("[{}]处理第{}篇文章，来源搜狐科技，标题\"{}\"，结果：非常规文章，{}", logVals);
            }
        } else {
            handleResult = delWebsiteSpider(websiteSpider);
            Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                    handleResult ? "删除成功" : "删除失败"};
            logger.warn("[{}]处理第{}篇文章，来源搜狐科技，标题\"{}\"，结果：网页内容为空，{}", logVals);
        }
    }

    /**
     * 抓取新浪科技文章
     *
     * @param websiteSpider
     * @param currentThreadName
     * @param currentChapter
     */
    private void extractSinaWebContent(WebsiteSpider websiteSpider, String currentThreadName, int currentChapter) {
        boolean handleResult;
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl, "ISO-8859-1");
        if (StringUtils.isNotEmpty(html)) {
            if (html.contains("id=\"artibody\"")) {
                try {
                    // 用网页声明的字符集对网页内容编码
                    html = charsetAnalyzeHtml(html);

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
                        for (String word : keywordsArr) {
                            keywordsBuilder.append(word);
                            keywordsBuilder.append(",");
                        }
                        article.setKeywords(keywordsBuilder.substring(0, keywordsBuilder.length() - 1));
                    }
                    // 抓取文章发布时间
                    Element pubTimeEl = doc.select("span.titer").first();
                    if (pubTimeEl != null) {
                        String pubTime = pubTimeEl.text();
                        if (pubTime != null) {
                            pubTime = pubTime.replace("年", "-").replace("月", "-").replace("日", "");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date = formatter.parse(pubTime);
                            article.setPubTime(date);
                        }
                    }
                    Element pubTimeWeiboEl = doc.select("span#pub_date").first();
                    if (pubTimeWeiboEl != null) {
                        String pubTime = pubTimeWeiboEl.html();
                        if (pubTime != null) {
                            if (pubTime.contains("&nbsp;")) {
                                pubTime = pubTime.replace("&nbsp;", " ").replace("日", "");
                            }
                            if (!pubTime.contains(" ")) {
                                pubTime = pubTime.replace("日", " ");
                            } else {
                                pubTime = pubTime.replace("日", "");
                            }
                            pubTime = pubTime.replace("年", "-").replace("月", "-");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date = formatter.parse(pubTime);
                            article.setPubTime(date);
                        }
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
                    handleResult = websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            handleResult ? "处理成功" : "已经被处理过"};
                    logger.info("[{}]处理第{}篇文章，来源新浪科技，标题\"{}\"，结果：{}", logVals);
                } catch (Exception e) {
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            e.getMessage(), websiteSpider.getOriginalUrl()};
                    logger.error("[{}]处理第{}篇文章，来源新浪科技，标题\"{}\"，结果异常：{}, 网址：{}", logVals);
                }
            } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
                handleResult = delWebsiteSpider(websiteSpider);
                Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                        handleResult ? "删除成功" : "删除失败"};
                logger.warn("[{}]处理第{}篇文章，来源新浪科技，标题\"{}\"，结果：非常规文章，{}", logVals);
            }
        } else {
            handleResult = delWebsiteSpider(websiteSpider);
            Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                    handleResult ? "删除成功" : "删除失败"};
            logger.warn("[{}]处理第{}篇文章，来源新浪科技，标题\"{}\"，结果：网页内容为空，{}", logVals);
        }
    }

    /**
     * 抓取腾讯科技文章
     *
     * @param websiteSpider
     * @param currentThreadName
     * @param currentChapter
     */
    private void extractQQWebContent(WebsiteSpider websiteSpider, String currentThreadName, int currentChapter) {
        boolean handleResult;
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (StringUtils.isNotEmpty(html)) {
            if (html.contains("id=\"Cnt-Main-Article-QQ\"")) {
                try {
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

                    // 判断本文是否由腾讯科技机器人Dreamwriter自动撰写
                    boolean isDreamwriter = false;
                    Element dreamwriter = doc.select("span.where").first();
                    if (dreamwriter != null) {
                        String dreamwriterText = dreamwriter.text();
                        if ("Dreamwriter".equals(dreamwriterText)) {
                            isDreamwriter = true;
                        }
                    }
                    if (!isDreamwriter) {
                        boolean compatibleModel = false; // 判断网页是否是兼容模式
                        Element compatibleEl = doc.select("meta[http-equiv=X-UA-Compatible]").first();
                        if (compatibleEl != null) {
                            String content = compatibleEl.attr("content");
                            if (!"IE=edge".equals(content)) {
                                compatibleModel = true;
                            }
                        }
                        // 抓取文章关键字
                        Element keywordsEl = doc.select("meta[name=keywords]").first();
                        if (keywordsEl != null) {
                            String keywordsText = keywordsEl.attr("content");
                            String[] keywordsArr;
                            if (compatibleModel) { // 兼容模式
                                keywordsArr = keywordsText.split(";");
                            } else { // 最新模式
                                keywordsArr = keywordsText.split(",");
                            }
                            if (keywordsArr.length > 1) {
                                StringBuilder keywordsBuilder = new StringBuilder();
                                for (int i = 1; i < keywordsArr.length; i++) {
                                    if (i != 1) {
                                        keywordsBuilder.append(",");
                                    }
                                    String keywords = keywordsArr[i];
                                    if (keywords.length() > 32) { // 关键字长度过长，检查是否可以再度拆分
                                        if (keywords.contains(" ")) {
                                            String[] miniKeyArr = keywords.split(" ");
                                            for (int j = 0; j < miniKeyArr.length; j++) {
                                                String miniWord = miniKeyArr[j];
                                                if (miniWord.length() < 32) {
                                                    if (j != 0) {
                                                        keywordsBuilder.append(",");
                                                    }
                                                    keywordsBuilder.append(miniWord);
                                                }
                                            }
                                        }
                                    } else {
                                        keywordsBuilder.append(keywords);
                                    }
                                }
                                article.setKeywords(keywordsBuilder.toString());
                            }
                        }
                        // 抓取文章作者（文章来源）
                        Element authorEl = doc.select("span.a_source").first();
                        if (authorEl != null) {
                            Element link = authorEl.select("a[href]").first();
                            String author;
                            if (link != null) {
                                author = link.text();
                            } else {
                                author = authorEl.text();
                            }
                            article.setAuthor(author);
                        }
                        // 抓取文章发布时间
                        Element pubTimeEl = doc.select("span.a_time").first();
                        if (pubTimeEl != null) {
                            String pubTime = pubTimeEl.text();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date date = formatter.parse(pubTime);
                            article.setPubTime(date);
                        }
                        Element pubTimeOldEl = doc.select("span.pubTime").first();
                        if (pubTimeOldEl != null) {
                            String pubTime = pubTimeOldEl.text();
                            pubTime = pubTime.replace("年", "-").replace("月", "-").replace("日", " ");
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

                        handleResult = websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
                        Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                                handleResult ? "处理成功" : "已经被处理过"};
                        logger.info("[{}]处理第{}篇文章，来源腾讯科技，标题\"{}\"，结果：{}", logVals);
                    } else {
                        handleResult = delWebsiteSpider(websiteSpider);
                        Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                                handleResult ? "删除成功" : "删除失败"};
                        logger.warn("[{}]处理完第{}篇文章，来源腾讯科技，标题\"{}\"，结果：本文由腾讯科技机器人Dreamwriter自动撰写，{}", logVals);
                    }
                } catch (Exception e) {
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            e.getMessage(), websiteSpider.getOriginalUrl()};
                    logger.error("[{}]处理第{}篇文章，来源腾讯科技，标题\"{}\"，结果异常：{}, 网址：{}", logVals);
                }
            } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
                handleResult = delWebsiteSpider(websiteSpider);
                Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                        handleResult ? "删除成功" : "删除失败"};
                logger.warn("[{}]处理完第{}篇文章，来源腾讯科技，标题\"{}\"，结果：非常规文章，{}", logVals);
            }
        } else {
            handleResult = delWebsiteSpider(websiteSpider);
            Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                    handleResult ? "删除成功" : "删除失败"};
            logger.warn("[{}]处理第{}篇文章，来源腾讯科技，标题\"{}\"，结果：网页内容为空，{}", logVals);
        }
    }

    /**
     * 抓取网易科技文章
     *
     * @param websiteSpider
     * @param currentThreadName
     * @param currentChapter
     */
    private void extractWangyiWebContent(WebsiteSpider websiteSpider, String currentThreadName, int currentChapter) {
        boolean handleResult;
        String originalUrl = websiteSpider.getOriginalUrl();
        String html = HttpUtil.httpClientGet(originalUrl);
        if (StringUtils.isNotEmpty(html)) {
            if (html.contains("class=\"post_text\"")) {
                try {
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
                    article.setPubTime(websiteSpider.getPubTime()); // 发布时间
                    articleContent.setSummary(websiteSpider.getSummary()); // 摘要

                    Document doc = Jsoup.parse(html);
                    doc.select("script,noscript,style,iframe,br").remove();
                    // 抓取文章关键字
                    Element keywordsEl = doc.select("meta[name=keywords]").first();
                    if (keywordsEl != null) {
                        String keywords = keywordsEl.attr("content");
                        String[] keywordsArr = keywords.split(",");
                        StringBuilder keywordsBuilder = new StringBuilder();
                        for (String word : keywordsArr) {
                            keywordsBuilder.append(word);
                            keywordsBuilder.append(",");
                        }
                        article.setKeywords(keywordsBuilder.substring(0, keywordsBuilder.length() - 1));
                    }
                    // 抓取文章作者（文章来源）
                    Element authorEl = doc.select("a#ne_article_source").first();
                    if (authorEl != null) {
                        String author = authorEl.text();
                        article.setAuthor(author);
                    }
                    // 抓取文章正文
                    Element content = doc.select("div#endText.post_text").first();
                    if (content != null) {
                        // 去掉广告
                        content.select("div.gg200x300").remove();
                        // 去掉视频
                        content.select("div.video-wrapper").remove();
                        String contentHtml = content.html();
                        contentHtml = removeUselessAttribute(contentHtml);
                        articleContent.setContent(contentHtml);
                    }
                    handleResult = websiteSpiderService.handleSpiderContent(websiteSpider, article, articleContent);
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            handleResult ? "处理成功" : "已经被处理过"};
                    logger.info("[{}]处理第{}篇文章，来源网易科技，标题\"{}\"，结果：{}", logVals);
                } catch (Exception e) {
                    Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                            e.getMessage(), websiteSpider.getOriginalUrl()};
                    logger.error("[{}]处理第{}篇文章，来源网易科技，标题\"{}\"，结果异常：{}, 网址：{}", logVals);
                }
            } else { // 不是一个正规的新闻页面，可能是一个幻灯片网页
                handleResult = delWebsiteSpider(websiteSpider);
                Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                        handleResult ? "删除成功" : "删除失败"};
                logger.info("[{}]处理完第{}篇文章，来源网易科技，标题\"{}\"，结果：非常规文章，{}", logVals);
            }
        } else {
            handleResult = delWebsiteSpider(websiteSpider);
            Object[] logVals = {currentThreadName, currentChapter, websiteSpider.getTitle(),
                    handleResult ? "删除成功" : "删除失败"};
            logger.warn("[{}]处理第{}篇文章，来源网易科技，标题\"{}\"，结果：网页内容为空，{}", logVals);
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
    private boolean delWebsiteSpider(WebsiteSpider websiteSpider) {
        WebsiteSpider record = new WebsiteSpider();
        WebsiteSpider example = new WebsiteSpider();
        String id = websiteSpider.getId();
        int version = websiteSpider.getVersion();
        int analysisCount = websiteSpider.getAnalysisCount();
        record.setAnalysisCount(analysisCount + 1);
        record.setVersion(version + 1);
        example.setId(id);
        example.setVersion(version);
        return websiteSpiderService.deleteByConditionSelective(record, example);
    }

    /**
     * 用网页声明的字符集对网页内容编码
     *
     * @param html 网页内容
     * @return 重新编码的网页内容
     * @throws UnsupportedEncodingException
     */
    private String charsetAnalyzeHtml(String html) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(html);
        Element charsetH4 = doc.select("meta[http-equiv=Content-type]").first();
        if (charsetH4 != null) {
            String content = charsetH4.attr("content");
            String pattern = ".*?charset=(.+)";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            if (m.find()) {
                String charset = m.group(1);
                if ("UTF-8".equalsIgnoreCase(charset)
                        || "GBK".equalsIgnoreCase(charset)
                        || "GB2312".equalsIgnoreCase(charset)) {
                    html = new String(html.getBytes("ISO-8859-1"), charset);
                }
            }
        }

        Element charsetH5 = doc.select("meta[charset]").first();
        if (charsetH5 != null) {
            String charset = charsetH5.attr("charset");
            if ("UTF-8".equalsIgnoreCase(charset)
                    || "GBK".equalsIgnoreCase(charset)
                    || "GB2312".equalsIgnoreCase(charset)) {
                html = new String(html.getBytes("ISO-8859-1"), charset);
            }
        }
        return html;
    }

    @Override
    public void repairWebArticlePubtime(int begin) {
        int currentChapter = 1;
        // 当前线程名称
        String currentThreadName = Thread.currentThread().getName();
        Map<String, Object> record = new HashMap<>();
        record.put("hql", "pub_time IS NULL");
        record.put("isDel", 0);
        Paging paging = new Paging(begin);
        List<Article> articles = articleService.selectPageByConditionNoOrder(record, paging);
        do {
            for (Article article : articles) {
                boolean handleResult;
                String originalUrl = article.getOriginalUrl();
                String html = HttpUtil.httpClientGet(originalUrl);
                if (StringUtils.isNotEmpty(html)) {
                    if (html.contains("id=\"Cnt-Main-Article-QQ\"")) {
                        try {
                            Document doc = Jsoup.parse(html);
                            doc.select("script,noscript,style,iframe,br").remove();

                            // 抓取文章发布时间
                            Element pubTimeEl = doc.select("span.a_time").first();
                            if (pubTimeEl != null) {
                                String pubTime = pubTimeEl.text();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date date = formatter.parse(pubTime);
                                article.setPubTime(date);
                            }
                            Element pubTimeOldEl = doc.select("span.pubTime").first();
                            if (pubTimeOldEl != null) {
                                String pubTime = pubTimeOldEl.text();
                                pubTime = pubTime.replace("年", "-").replace("月", "-").replace("日", " ");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date date = formatter.parse(pubTime);
                                article.setPubTime(date);
                            }

                            handleResult = articleService.repairWebArticlePubtime(article);
                            Object[] logVals = {currentThreadName, currentChapter, article.getTitle()};
                            if (handleResult) {
                                logger.info("[{}]修复发布时间第{}篇，来源腾讯科技，标题\"{}\"，结果：成功", logVals);
                            } else {
                                logger.warn("[{}]修复发布时间第{}篇，来源腾讯科技，标题\"{}\"，结果：已经处理过", logVals);
                            }
                        } catch (Exception e) {
                            Object[] logVals = {currentThreadName, currentChapter, article.getTitle(),
                                    e.getMessage(), article.getOriginalUrl()};
                            logger.error("[{}]修复发布时间第{}篇，来源腾讯科技，标题\"{}\"，结果异常：{}, 网址：{}", logVals);
                        }
                    }
                } else {
                    Object[] logVals = {currentThreadName, currentChapter, article.getTitle()};
                    logger.warn("[{}]修复发布时间第{}篇，来源腾讯科技，标题\"{}\"，结果：网页内容为空", logVals);
                }
                currentChapter++;
            }
            articles = articleService.selectPageByConditionNoOrder(record, paging);
        } while (articles != null && !articles.isEmpty());
    }
}