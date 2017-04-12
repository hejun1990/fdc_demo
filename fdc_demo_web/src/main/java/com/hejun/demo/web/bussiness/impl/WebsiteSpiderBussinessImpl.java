package com.hejun.demo.web.bussiness.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import com.hejun.demo.service.inter.service.sitemanager.TagStoreService;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private WebsiteSpiderService websiteSpiderService;
    @Autowired
    private TagStoreService tagStoreService;

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
        // 目前搜狐科技新闻数据接口最多返回到50页内容
        for (int page = 1; page <= 50; page++) {
            String spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
            String content = HttpUtil.httpClientGet(spiderUrl);
            // 处理返回的json字符串，去掉首尾的非json格式字符
            if (StringUtils.isNotEmpty(content)) {
                content = content.substring(7, content.length() - 2);
                JSONArray jsonArray = JSONArray.parseArray(content);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject != null) {
                        WebsiteSpider websiteSpider = new WebsiteSpider();
                        String id = Long.toString(jsonObject.getLongValue("id"));
                        String authorId = Integer.toString(jsonObject.getIntValue("authorId"));
                        String title = jsonObject.getString("title");
                        String authorName = jsonObject.getString("authorName");
                        String picUrl = jsonObject.getString("picUrl");
                        if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(authorId)
                                && StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(authorName)) {
                            websiteSpider.setTitle(title);
                            websiteSpider.setAuthor(authorName);
                            websiteSpider.setOriginalSiteCode(SpiderEntry.SOHU.getCode());
                            websiteSpider.setOriginalSiteName(SpiderEntry.SOHU.getName());
                            String visitUrl = SpiderEntry.SOHU.getVisitUrl();
                            visitUrl = visitUrl.replace("{ID}", id)
                                    .replace("{AUTHORID}", authorId);
                            websiteSpider.setOriginalUrl(visitUrl);
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
                                    if(j != 0) {
                                        tagsBuilder.append(",");
                                    }
                                    tagsBuilder.append(tagName);
                                    Map<String, Object> tagsParams = new HashMap<String, Object>();
                                    tagsParams.put("tagName", tagName);
                                    int tagStoreCount = tagStoreService.countByCondition(tagsParams);
                                    // 如果标签库中没有该标签，则插入新标签
                                    if(tagStoreCount == 0){
                                        TagStore tagStore = new TagStore();
                                        tagStore.setTagName(tagName);
                                        tagStoreService.insertSelective(tagStore);
                                    }
                                }
                                websiteSpider.setTags(tagsBuilder.toString());
                            }
                            Map<String, Object> websiteSpiderParams = new HashMap<String, Object>();
                            websiteSpiderParams.put("originalUrl", websiteSpider.getOriginalUrl());
                            int websiteSpiderCount = websiteSpiderService.countByCondition(websiteSpiderParams);
                            // 如果访问网址没有重复，则插入原始文章信息
                            if(websiteSpiderCount == 0) {
                                websiteSpiderService.insertSelective(websiteSpider);
                            }
                        }
                    }
                }
            }
        }
    }
}
