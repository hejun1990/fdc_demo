package com.hejun.demo.web.controller;

import com.hejun.demo.web.bussiness.ArticleAnalysisBussiness;
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

    @Autowired
    private ArticleAnalysisBussiness articleAnalysisBussiness;

    private ExecutorService fixedThreadPool;

    @PostConstruct
    public void startUp() {
        fixedThreadPool = Executors.newFixedThreadPool(10);
        webSpider();
//        webArticleAnalysis();
    }

    private void webSpider() {
        logger.info("开启网络爬虫");
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
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 腾讯科技资讯
                websiteSpiderBussiness.qqITSpider();
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 网易科技资讯
                websiteSpiderBussiness.wangyiITSpider();
            }
        });
    }

    private void webArticleAnalysis() {
        logger.info("开始对爬取网页进行正文解析");
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.extractWebContent();
            }
        });
    }
}
