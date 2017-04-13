package com.hejun.demo.web.controller;

import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 搜狐科技资讯
                websiteSpiderBussiness.sohuITSpider();
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 新浪科技资讯
                websiteSpiderBussiness.sinaITSpider();
            }
        });
    }
}
