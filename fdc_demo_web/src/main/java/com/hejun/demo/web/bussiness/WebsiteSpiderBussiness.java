package com.hejun.demo.web.bussiness;

import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
public interface WebsiteSpiderBussiness {
    /**
     * 添加原始文章记录
     * @param websiteSpider
     * @return
     */
    boolean addWebsiteSpider(WebsiteSpider websiteSpider);

    List<WebsiteSpider> selectByExample(WebsiteSpiderExample example);

    /**
     * 搜狐科技文章爬虫
     */
    void sohuITSpider();

    /**
     * 新浪科技爬虫
     */
    void sinaITSpider();

    /**
     * 腾讯科技爬虫
     */
    void qqITSpider();
}
