package com.hejun.demo.web.bussiness.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hejun.demo.service.inter.domain.generation.KeywordsStore;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import com.hejun.demo.service.inter.service.sitemanager.KeywordsStoreService;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import com.hejun.demo.web.enumeration.SpiderEntry;
import com.hejun.demo.web.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
@Service("websiteSpiderBussiness")
public class WebsiteSpiderBussinessImpl implements WebsiteSpiderBussiness {
    private static Logger logger = LoggerFactory.getLogger(WebsiteSpiderBussinessImpl.class);

    @Autowired
    private WebsiteSpiderService websiteSpiderService;
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
        String currentThreadName = Thread.currentThread().getName();
        //原始爬虫地址，需要替换{PAGE}参数
        String ori_spiderUrl = SpiderEntry.SOHU.getSpiderUrl();
        int page = 1;
        try {
            long startTime = System.currentTimeMillis();
            // 目前搜狐科技新闻数据接口最多返回到50页内容
            for (; page <= 50; page++) {
                String spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
                String content = HttpUtil.httpClientGet(spiderUrl);
                // 处理返回的json字符串
                if (StringUtils.isNotEmpty(content)) {
                    // 去掉首尾的非json格式字符
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
                                // 处理关键字
                                JSONArray tags = jsonObject.getJSONArray("tags");
                                if (tags != null && tags.size() > 0) {
                                    StringBuilder keywordsBuilder = new StringBuilder();
                                    for (int j = 0; j < tags.size(); j++) {
                                        JSONObject tag = tags.getJSONObject(j);
                                        String tagName = tag.getString("name");
                                        if (j != 0) {
                                            keywordsBuilder.append(",");
                                        }
                                        keywordsBuilder.append(tagName);
                                        Map<String, Object> keywordsParams = new HashMap<>();
                                        keywordsParams.put("keywords", tagName);
                                        int keywordsStoreCount = keywordsStoreService.countByCondition(keywordsParams);
                                        // 如果关键字库中没有该关键字，则插入新关键字
                                        if (keywordsStoreCount == 0) {
                                            KeywordsStore keywordsStore = new KeywordsStore();
                                            keywordsStore.setKeywords(tagName);
                                            keywordsStoreService.insertSelective(keywordsStore);
                                        }
                                    }
                                    websiteSpider.setKeywords(keywordsBuilder.toString());
                                }
                                websiteSpiderService.insertSelective(websiteSpider);
                            }
                        }
                    } else {
                        logger.warn("[{}]搜狐科技爬虫在第{}页无法获取数据", currentThreadName, page);
                    }
                }
                logger.info("[{}]搜狐科技爬虫爬完第{}页。", currentThreadName, page);
            }
            long endTime = System.currentTimeMillis();
            Object[] logVals = {currentThreadName, (endTime - startTime) / (1000 * 60), ((endTime - startTime) % (1000 * 60)) / 1000};
            logger.info("[{}]搜狐科技爬虫任务完成，耗时{}分{}秒。", logVals);
        } catch (Exception e) {
            Object[] logVals = {currentThreadName, page, e.getMessage()};
            logger.info("[{}]搜狐科技爬虫爬到第{}页出错：{}", logVals);
        }
    }

    @Override
    public void sinaITSpider() {
        String currentThreadName = Thread.currentThread().getName();
        //原始爬虫地址，需要替换{PAGE}参数
        String ori_spiderUrl = SpiderEntry.SINA.getSpiderUrl();
        int page = 1; // 当前页
        int total = 1; // 从新浪获取的总条数，验证用
        int totalPage = 1; // 总页数
        String spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
        String content = HttpUtil.httpClientGet(spiderUrl);
        // 获取新闻列表总页数(每页30条)
        if (StringUtils.isNotEmpty(content)) {
            // 去掉首尾的非json格式字符
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
                logger.warn("[{}]新浪科技爬虫无法取到第{}页数据", currentThreadName, page);
            }
        }
        try {
            long startTime = System.currentTimeMillis();
            // 开始从第1页读取信息
            for (; page <= totalPage; page++) {
                spiderUrl = ori_spiderUrl.replace("{PAGE}", String.valueOf(page));
                content = HttpUtil.httpClientGet(spiderUrl);
                if (StringUtils.isNotEmpty(content)) {
                    // 去掉首尾的非json格式字符
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
                                // 要乘以1000，因为爬取的时间是秒要转换成毫秒
                                long ctime = article.getLongValue("ctime") * 1000;
                                websiteSpider.setPubTime(new Date(ctime));
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
                                if (imgFirstChar == '{') {
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
                                    Map<String, Object> keywordsParams = new HashMap<>();
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
                        logger.warn("[{}]新浪科技爬虫无法取到第{}页数据", currentThreadName, page);
                    }
                }
                logger.info("[{}]新浪科技爬虫爬完第{}页。", currentThreadName, page);
            }
            long endTime = System.currentTimeMillis();
            Object[] logVals = {currentThreadName, (endTime - startTime) / (1000 * 60), ((endTime - startTime) % (1000 * 60)) / 1000};
            logger.info("[{}]新浪科技爬虫任务完成，耗时{}分{}秒。", logVals);
        } catch (Exception e) {
            Object[] logVals = {currentThreadName, page, e.getMessage()};
            logger.error("[{}]新浪科技爬虫爬到第{}页出错：{}", logVals);
        }
    }

    @Override
    public void qqITSpider() {
        String currentThreadName = Thread.currentThread().getName();
        //原始爬虫地址
        String ori_spiderUrl = SpiderEntry.QQ.getSpiderUrl();
        int totalDay = 365 * 3; // 从当天开始爬取过去3年的历史数据
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long startTime = System.currentTimeMillis();
        for (int amount = 0; amount < totalDay; amount++) {
            Map<String, String> replaceMap = getQQSpiderUrlReplace(-amount, formatter);
            handleQQITContext(ori_spiderUrl, replaceMap, 1, currentThreadName);
        }
        long endTime = System.currentTimeMillis();
        Object[] logVals = {currentThreadName, (endTime - startTime) / (1000 * 60), ((endTime - startTime) % (1000 * 60)) / 1000};
        logger.info("[{}]腾讯科技爬虫任务完成，耗时{}分{}秒。", logVals);
    }

    /**
     * 处理腾讯科技列表网页信息
     *
     * @param ori_spiderUrl
     * @param dateMap
     * @param page
     */
    private void handleQQITContext(String ori_spiderUrl, Map<String, String> dateMap, int page,
                                   String currentThreadName) {
        try {
            String spiderUrl;
            if (page > 1) {
                spiderUrl = ori_spiderUrl.replace("{YEAR_MONTH}", dateMap.get("year_month"))
                        .replace("{DAY_PAGE}", dateMap.get("day") + "_" + page);
            } else {
                spiderUrl = ori_spiderUrl.replace("{YEAR_MONTH}", dateMap.get("year_month"))
                        .replace("{DAY_PAGE}", dateMap.get("day"));
            }
            String content = HttpUtil.httpClientGet(spiderUrl);
            if (StringUtils.isNotEmpty(content)) {
                String pattern = "<a target=\"_blank\" href=\"(http://tech.qq.com/a/"
                        + dateMap.get("year_month_day") + "/(?:\\w)+.htm)\">(.*?)</a>";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(content);
                while (m.find()) {
                    String url = m.group(1);
                    String title = m.group(2);
                    Map<String, Object> websiteSpiderParams = new HashMap<>();
                    websiteSpiderParams.put("originalUrl", url);
                    int websiteSpiderCount = websiteSpiderService.countByCondition(websiteSpiderParams);
                    // 如果访问网址重复，跳过本次循环
                    if (websiteSpiderCount > 0) {
                        continue;
                    }
                    WebsiteSpider websiteSpider = new WebsiteSpider();
                    websiteSpider.setOriginalUrl(url);
                    websiteSpider.setOriginalSiteCode(SpiderEntry.QQ.getCode());
                    websiteSpider.setOriginalSiteName(SpiderEntry.QQ.getName());
                    websiteSpider.setTitle(title);
                    websiteSpider.setAuthor(SpiderEntry.QQ.getName());
                    websiteSpiderService.insertSelective(websiteSpider);
                }
                Object[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                        dateMap.get("day"), page};
                logger.info("[{}]腾讯科技爬虫爬完{}年{}月{}日第{}页数据。", logVals);
                if (isNextPage(content)) {
                    handleQQITContext(ori_spiderUrl, dateMap, ++page, currentThreadName);
                }
            } else {
                Object[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                        dateMap.get("day"), page};
                logger.warn("[{}]腾讯科技爬虫无法取到{}年{}月{}日第{}页数据。", logVals);
            }
        } catch (Exception e) {
            Object[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                    dateMap.get("day"), page, e.getMessage()};
            logger.error("[{}]腾讯科技爬虫爬取{}年{}月{}日第{}页报错：{}。", logVals);
        }
    }

    @Override
    public void wangyiITSpider() {
        String currentThreadName = Thread.currentThread().getName();
        long startTime = System.currentTimeMillis();
        String today_spiderUrl = "http://tech.163.com/special/00094IHV/news_json.js?" + Math.random();
        SimpleDateFormat yMd_formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yMd_Hms_Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> todayMap = getWangyiSpiderUrlReplace(0, yMd_formatter);
        // 爬取今日新闻
        handleWangyiITContext(today_spiderUrl, todayMap, yMd_Hms_Formatter, currentThreadName);

        //原始爬虫地址
        String ori_spiderUrl = SpiderEntry.WANGYI.getSpiderUrl();
        int totalDay = 365 * 3; // 从当天开始爬取过去3年的历史数据
        for (int amount = 1; amount <= totalDay; amount++) {
            Map<String, String> dateMap = getWangyiSpiderUrlReplace(-amount, yMd_formatter);
            String spiderUrl = ori_spiderUrl.replace("{year_month}", dateMap.get("year_month"))
                    .replace("{day}", dateMap.get("day"))
                    .replace("{random}", Double.toString(Math.random()));
            handleWangyiITContext(spiderUrl, dateMap, yMd_Hms_Formatter, currentThreadName);
        }
        long endTime = System.currentTimeMillis();
        Object[] logVals = {currentThreadName, (endTime - startTime) / (1000 * 60), ((endTime - startTime) % (1000 * 60)) / 1000};
        logger.info("[{}]网易科技爬虫任务完成，耗时{}分{}秒。", logVals);
    }

    private void handleWangyiITContext(String spiderUrl, Map<String, String> dateMap, SimpleDateFormat formatter,
                                       String currentThreadName) {
        try {
            String content = HttpUtil.httpClientGet(spiderUrl, "GBK");
            // 处理返回的json字符串
            if (StringUtils.isNotEmpty(content)) {
                // 去掉首尾的非json格式字符
                content = content.substring(9, content.length() - 1);
                JSONObject jsonObject = JSONObject.parseObject(content);
                if (jsonObject != null) {
                    JSONArray news = jsonObject.getJSONArray("news");
                    for (int i = 0; i < news.size(); i++) {
                        JSONArray array = news.getJSONArray(i);
                        for (int j = 0; j < array.size(); j++) {
                            JSONObject article = array.getJSONObject(j);
                            String title = article.getString("t");
                            String url = article.getString("l");
                            String pubTime = article.getString("p");
                            if (StringUtils.isEmpty(title) || StringUtils.isEmpty(url)) {
                                continue;
                            }
                            title = title.trim();
                            Map<String, Object> websiteSpiderParams = new HashMap<>();
                            websiteSpiderParams.put("originalUrl", url);
                            int websiteSpiderCount = websiteSpiderService.countByCondition(websiteSpiderParams);
                            // 如果访问网址重复，跳过本次循环
                            if (websiteSpiderCount > 0) {
                                continue;
                            }
                            WebsiteSpider websiteSpider = new WebsiteSpider();
                            websiteSpider.setOriginalUrl(url);
                            websiteSpider.setOriginalSiteCode(SpiderEntry.WANGYI.getCode());
                            websiteSpider.setOriginalSiteName(SpiderEntry.WANGYI.getName());
                            websiteSpider.setTitle(title);
                            websiteSpider.setPubTime(formatter.parse(pubTime));
                            websiteSpiderService.insertSelective(websiteSpider);
                        }
                    }
                    Object[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                            dateMap.get("day")};
                    logger.info("[{}]网易科技爬虫爬完{}年{}月{}日数据。", logVals);
                } else {
                    String[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                            dateMap.get("day")};
                    logger.warn("[{}]网易科技爬虫无法取到{}年{}月{}日数据。", logVals);
                }
            }
        } catch (Exception e) {
            String[] logVals = {currentThreadName, dateMap.get("year"), dateMap.get("month"),
                    dateMap.get("day"), e.getMessage()};
            logger.error("[{}]网易科技爬虫爬取{}年{}月{}日数据报错：{}。", logVals);
        }
    }

    /**
     * 获取腾讯科技地址动态参数
     *
     * @param amount
     * @param formatter
     * @return
     */
    private Map<String, String> getQQSpiderUrlReplace(int amount, SimpleDateFormat formatter) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount); //把日期往后增加一天.整数往后推,负数往前移动
        String dateStr = formatter.format(calendar.getTime());
        String[] dataArr = dateStr.split("-");
        String year = dataArr[0];
        String month = dataArr[1];
        String day = dataArr[2];
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);
        map.put("year_month", year + month);
        map.put("year_month_day", year + month + day);
        return map;
    }

    /**
     * 判断腾讯科技列表页是否有下一页
     *
     * @param content
     * @return
     */
    private boolean isNextPage(String content) {
        String pattern = "<span class=\"na\">下一页&gt;</span>";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        return !m.find();
    }

    /**
     * 获取网易科技地址动态参数
     *
     * @param amount
     * @param formatter
     * @return
     */
    private Map<String, String> getWangyiSpiderUrlReplace(int amount, SimpleDateFormat formatter) {
        Map<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount); //把日期往后增加一天.整数往后推,负数往前移动
        String dateStr = formatter.format(calendar.getTime());
        String[] dataArr = dateStr.split("-");
        String year = dataArr[0];
        String month = dataArr[1];
        String day = dataArr[2];
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);
        map.put("year_month", year + "-" + month);
        return map;
    }
}