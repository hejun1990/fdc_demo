package com.hejun.demo.web.enumeration;

/**
 * 爬虫访问入口
 * <p>
 * Created by hejun-FDC on 2017/4/11.
 */
public enum SpiderEntry {
    SOHU(1, "搜狐科技",
            "http://v2.sohu.com/public-api/feed?scene=CHANNEL&sceneId=30&page={PAGE}&size=20&callback=hj",
            "http://www.sohu.com/a/{ID}_{AUTHORID}"),
    SINA(2, "新浪科技",
            "http://feed.mix.sina.com.cn/api/roll/get?pageid=1&lid=21&num=30&versionNumber=1.2.8&page={PAGE}&encode=utf-8&callback=feedCardJsonpCallback"),
    QQ(3, "腾讯科技", "http://tech.qq.com/l/{YEAR_MONTH}/scroll_{DAY_PAGE}.htm"),
    WANGYI(4, "网易科技", "http://snapshot.news.163.com/wgethtml/http+!!tech.163.com!special!00094IHV!news_json.js/{year_month}/{day}/0.js?{random}");

    /**
     * 网站编号
     */
    private int code;
    /**
     * 网站名称
     */
    private String name;
    /**
     * 爬虫入口地址
     */
    private String spiderUrl;
    /**
     * 文章访问地址
     */
    private String visitUrl;

    SpiderEntry(int code, String name, String spiderUrl) {
        this.code = code;
        this.name = name;
        this.spiderUrl = spiderUrl;
    }

    SpiderEntry(int code, String name, String spiderUrl, String visitUrl) {
        this.code = code;
        this.name = name;
        this.spiderUrl = spiderUrl;
        this.visitUrl = visitUrl;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpiderUrl() {
        return spiderUrl;
    }

    public String getVisitUrl() {
        return visitUrl;
    }
}
