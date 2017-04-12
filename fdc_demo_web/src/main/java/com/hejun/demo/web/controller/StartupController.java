package com.hejun.demo.web.controller;

import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Created by hejun-FDC on 2017/4/10.
 */
@Controller
public class StartupController {
    private static Logger logger = LoggerFactory.getLogger(StartupController.class);

    @Autowired
    private WebsiteSpiderBussiness websiteSpiderBussiness;

    @PostConstruct
    public void WebSpider() {
        logger.info("开启网络爬虫");
        // 搜狐科技资讯
        websiteSpiderBussiness.sohuITSpider();
    }
}
