package com.hejun.demo.web.bussiness.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import com.hejun.demo.service.inter.service.sitemanager.KeywordsStoreService;
import com.hejun.demo.service.inter.service.sitemanager.TagStoreService;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import com.hejun.demo.web.utils.UnicodeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
@Service("websiteSpiderBussiness")
public class WebsiteSpiderBussinessImpl implements WebsiteSpiderBussiness {
    private static Logger logger = LoggerFactory.getLogger(WebsiteSpiderBussinessImpl.class);

    @Autowired
    private WebsiteSpiderService websiteSpiderService;
    @Autowired
    private TagStoreService tagStoreService;
    @Autowired
    private KeywordsStoreService keywordsStoreService;

    @Override
    public boolean addWebsiteSpider(WebsiteSpider websiteSpider) {

        return websiteSpiderService.insertSelective(websiteSpider);
    }

    @Override
    public List<WebsiteSpider> selectByExample(WebsiteSpiderExample example) {
        return websiteSpiderService.selectByExample(example);
    }

    @Override
    public void sohuITSpider() {
        //原始爬虫地址，需要替换{PAGE}参数
        String ori_spiderUrl = SpiderEntry.SOHU.getSpiderUrl();
        int page = 1;
        try {
            // 目前搜狐科技新闻数据接口最多返回到50页内容
            for (; page <= 50; page++) {
                String spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
                String content = HttpUtil.httpClientGet(spiderUrl);
                // 处理返回的json字符串，去掉首尾的非json格式字符
                if (StringUtils.isNotEmpty(content)) {
                    content = content.substring(7, content.length() - 2);
                    JSONArray jsonArray = JSONArray.parseArray(content);
                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject != null) {
                                String id = Long.toString(jsonObject.getLongValue("id"));
                                String authorId = Integer.toString(jsonObject.getIntValue("authorId"));
                                String title = jsonObject.getString("title");
                                // 如果id、authorId或title为空，跳过本次循环
                                if (StringUtils.isEmpty(id) || StringUtils.isEmpty(authorId)
                                        || StringUtils.isEmpty(title)) {
                                    continue;
                                }
                                String visitUrl = SpiderEntry.SOHU.getVisitUrl();
                                visitUrl = visitUrl.replace("{ID}", id)
                                        .replace("{AUTHORID}", authorId);
                                Map<String, Object> websiteSpiderParams = new HashMap<>();
                                websiteSpiderParams.put("originalUrl", visitUrl);
                                int websiteSpiderCount = websiteSpiderService.countByCondition(websiteSpiderParams);
                                // 如果访问网址重复，跳过本次循环
                                if (websiteSpiderCount > 0) {
                                    continue;
                                }
                                WebsiteSpider websiteSpider = new WebsiteSpider();
                                websiteSpider.setTitle(title);
                                websiteSpider.setOriginalUrl(visitUrl);
                                String authorName = jsonObject.getString("authorName");
                                websiteSpider.setAuthor(
                                        StringUtils.isNotEmpty(authorName) ? authorName
                                                : SpiderEntry.SOHU.getName());
                                websiteSpider.setOriginalSiteCode(SpiderEntry.SOHU.getCode());
                                websiteSpider.setOriginalSiteName(SpiderEntry.SOHU.getName());
                                String picUrl = jsonObject.getString("picUrl");
                                if (StringUtils.isNotEmpty(picUrl)) {
                                    if (!picUrl.contains("http:") && !picUrl.contains("https:")) {
                                        picUrl = "http:" + picUrl;
                                    }
                                    websiteSpider.setPicUrl(picUrl);
                                }
                                // 处理标签
                                JSONArray tags = jsonObject.getJSONArray("tags");
                                if (tags != null && tags.size() > 0) {
                                    StringBuilder tagsBuilder = new StringBuilder();
                                    for (int j = 0; j < tags.size(); j++) {
                                        JSONObject tag = tags.getJSONObject(j);
                                        String tagName = tag.getString("name");
                                        if (j != 0) {
                                            tagsBuilder.append(",");
                                        }
                                        tagsBuilder.append(tagName);
                                        Map<String, Object> tagsParams = new HashMap<>();
                                        tagsParams.put("tagName", tagName);
                                        int tagStoreCount = tagStoreService.countByCondition(tagsParams);
                                        // 如果标签库中没有该标签，则插入新标签
                                        if (tagStoreCount == 0) {
                                            TagStore tagStore = new TagStore();
                                            tagStore.setTagName(tagName);
                                            tagStoreService.insertSelective(tagStore);
                                        }
                                    }
                                    websiteSpider.setTags(tagsBuilder.toString());
                                }
                                websiteSpiderService.insertSelective(websiteSpider);
                            }
                        }
                    } else {
                        logger.warn("搜狐科技爬虫在第{}页无法获取数据", page);
                    }
                }
                logger.info("搜狐科技爬虫爬完第{}页。", page);
            }
        } catch (Exception e) {
            logger.info("搜狐科技爬虫爬到第{}页出错：{}", page, e.toString());
        }
    }

    @Override
    public void sinaITSpider() {
        //原始爬虫地址，需要替换{PAGE}参数
        String ori_spiderUrl = SpiderEntry.SINA.getSpiderUrl();
        int page = 1; // 当前页
        int total = 1; // 从新浪获取的总条数，验证用
        int totalPage = 1; // 总页数
        String spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
        String content = HttpUtil.httpClientGet(spiderUrl);
        // 获取新闻列表总页数(每页30条)
        if (StringUtils.isNotEmpty(content)) {
            content = content.substring(26, content.length() - 14);
            JSONObject jsonObject = JSONObject.parseObject(content);
            if (jsonObject != null) {
                JSONObject result = jsonObject.getJSONObject("result");
                JSONObject status = result.getJSONObject("status");
                int code = status.getIntValue("code");
                String msg = status.getString("msg");
                if (code == 0 && "succ".equals(msg)) {
                    total = result.getIntValue("total");
                    totalPage = total % 30 == 0 ? total / 30 : total / 30 + 1;
                }
            } else {
                logger.warn("新浪科技爬虫在第{}页无法获取数据", page);
            }
        }
        try {
            // 开始从第1页读取信息
            for (; page <= totalPage; page++) {
                spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
                content = HttpUtil.httpClientGet(spiderUrl);
                if (StringUtils.isNotEmpty(content)) {
                    content = content.substring(26, content.length() - 14);
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    if (jsonObject != null) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        JSONObject status = result.getJSONObject("status");
                        int code = status.getIntValue("code");
                        String msg = status.getString("msg");
                        if (code == 0 && "succ".equals(msg)) {
                            int curTotal = result.getIntValue("total");
                            // 如果新获取的总条数和之前获取的不相等
                            // 重新计算新的总页数
                            if (curTotal != total) {
                                total = curTotal;
                                totalPage = total % 30 == 0 ? total / 30 : total / 30 + 1;
                            }
                            // 获取新闻信息
                            JSONArray data = result.getJSONArray("data");
                            for (int i = 0; i < data.size(); i++) {
                                JSONObject article = data.getJSONObject(i);
                                String title = article.getString("title");
                                String url = article.getString("url");
                                if (StringUtils.isEmpty(title) || StringUtils.isEmpty(url)) {
                                    continue;
                                }
                                Map<String, Object> websiteSpiderParams = new HashMap<>();
                                websiteSpiderParams.put("originalUrl", url);
                                int websiteSpiderCount = websiteSpiderService.countByCondition(websiteSpiderParams);
                                // 如果访问网址重复，跳过本次循环
                                if (websiteSpiderCount > 0) {
                                    continue;
                                }
                                WebsiteSpider websiteSpider = new WebsiteSpider();
                                websiteSpider.setOriginalUrl(url);
                                websiteSpider.setOriginalSiteCode(SpiderEntry.SINA.getCode());
                                websiteSpider.setOriginalSiteName(SpiderEntry.SINA.getName());
                                websiteSpider.setTitle(title);
                                String stitle = article.getString("stitle");
                                if (StringUtils.isNotEmpty(stitle)) {
                                    websiteSpider.setViceTitle(stitle);
                                }
                                String summary = article.getString("summary");
                                if (StringUtils.isNotEmpty(summary)) {
                                    websiteSpider.setSummary(summary);
                                }
                                String author = article.getString("author");
                                String media_name = article.getString("media_name");
                                if (StringUtils.isEmpty(author)) {
                                    if (StringUtils.isEmpty(media_name)) {
                                        author = SpiderEntry.SINA.getName();
                                    } else {
                                        author = media_name;
                                    }
                                }
                                websiteSpider.setAuthor(author);
                                String imgJsonStr = article.getString("img");
                                char imgFirstChar = imgJsonStr.charAt(0);
                                if(imgFirstChar == '{') {
                                    JSONObject img = article.getJSONObject("img");
                                    String u = img.getString("u");
                                    if (StringUtils.isNotEmpty(u)) {
                                        if (!u.contains("http:") && !u.contains("https:")) {
                                            u = "http:" + u;
                                        }
                                        websiteSpider.setPicUrl(u);
                                    }
                                }
                                String keywords = article.getString("keywords");
                                if (StringUtils.isNotEmpty(keywords)) {
                                    websiteSpider.setKeywords(keywords);
                                }
                                websiteSpiderService.insertSelective(websiteSpider);

                                // 处理关键字信息
                                String[] keywordsArray = keywords.split(",");
                                for (String word : keywordsArray) {
                                    Map<String, Object> keywordsParams = new HashMap<String, Object>();
                                    keywordsParams.put("keywords", word);
                                    int keywordsStoreCount = keywordsStoreService.countByCondition(keywordsParams);
                                    // 如果关键字库中没有该关键字，则插入新关键字
                                    if (keywordsStoreCount == 0) {
                                        KeywordsStore keywordsStore = new KeywordsStore();
                                        keywordsStore.setKeywords(word);
                                        keywordsStoreService.insertSelective(keywordsStore);
                                    }
                                }
                            }
                        }
                    } else {
                        logger.warn("新浪科技爬虫在第{}页无法获取数据", page);
                    }
                }
                logger.info("新浪科技爬虫爬完第{}页。", page);
            }
        } catch (Exception e) {
            logger.error("新浪科技爬虫爬到第{}页出错：{}", page, e.toString());
        }
    }
}
