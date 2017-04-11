package com.hejun.demo.web.enumeration;

/**
 * 爬虫访问入口
 *
 * Created by hejun-FDC on 2017/4/11.
 */
public enum SpiderEntry {
    SOHU(1, "搜狐科技", "http://v2.sohu.com/public-api/feed?scene=CHANNEL&sceneId=30&page={PAGE}&size=20&callback=hj"),
    SINA(2, "新浪科技", ""),
    QQ(3, "腾讯科技", ""),
    WANGYI(4, "网易科技", "");

    private int code;
    private String name;
    private String url;

    private SpiderEntry(int code, String name, String url) {
        this.code = code;
        this.name = name;
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
