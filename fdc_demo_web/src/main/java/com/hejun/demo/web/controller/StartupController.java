package com.hejun.demo.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Created by hejun-FDC on 2017/4/10.
 */
@Controller
public class StartupController {
    private static Logger logger = LoggerFactory.getLogger(StartupController.class);

    @PostConstruct
    public void WebSpider() {
        String url = "http://v2.sohu.com/public-api/feed?scene=CHANNEL&sceneId=30&page=1&size=20&callback=hj";
        String content = HttpUtil.httpClientGet(url);
        logger.info(content);
    }

    /**
     * 搜狐科技爬虫
     */
    private void sohuITSpider() {
        String ori_url = SpiderEntry.SOHU.getUrl(); // 原始url，含有需替换的page参数
        // 目前搜狐科技新闻数据接口最多返回到50页内容
        for (int page = 1; page <= 50; page++) {
            String web_url = ori_url.replace("{PAGE}", String.valueOf(page));
            String content = HttpUtil.httpClientGet(web_url);
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
                        if(!picUrl.contains("http:") && !picUrl.contains("https:")) {
                            picUrl = "http:" + picUrl;
                        }
                        JSONArray tags = jsonObject.getJSONArray("tags");
                        if(tags != null && tags.size() > 0) {
                            // 处理标签
                            for (int j = 0; j < tags.size(); j++) {
                                
                            }
                        }
                    }
                }
            }
        }
    }
}
