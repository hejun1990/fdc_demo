package com.hejun.demo.web.controller;

import com.hejun.demo.service.inter.rocketmq.RMQProducer;
import com.hejun.demo.web.bussiness.ArticleAnalysisBussiness;
import com.hejun.demo.web.bussiness.WebsiteSpiderBussiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

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

    @Value("${redis_key_prefix}")
    private String nhsRedisKeyPrefix;

//    @Autowired
//    private JedisClusterService jedisClusterService;

    @Autowired
    private RMQProducer rmqProducer;

    private ExecutorService fixedThreadPool;

    @PostConstruct
    public void startUp() {
//        testRocketMQ();
//        fixedThreadPool = Executors.newFixedThreadPool(10);
//        webSpider();
//        webArticleAnalysis();
//        repairWebArticlePubtime();
    }

    /**
     * 新闻爬虫
     */
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

    /**
     * 文章链接解析
     */
    private void webArticleAnalysis() {
        logger.info("开始对爬取网页进行正文解析");
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.extractWebContent(1);
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.extractWebContent(2);
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.extractWebContent(3);
            }
        });
    }

    /**
     * 修补文章发布时间
     */
    private void repairWebArticlePubtime() {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.repairWebArticlePubtime(1);
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.repairWebArticlePubtime(2);
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                articleAnalysisBussiness.repairWebArticlePubtime(3);
            }
        });
    }

    /**
     * 测试redis
     */
    private void testRedis() {
//        if (jedisClusterService.exists(nhsRedisKeyPrefix + "_name", 1)) {
//            String name = jedisClusterService.get(nhsRedisKeyPrefix + "_name", 1);
//            logger.info("get redis name : {}", name);
//        } else {
//            String name = "hejun";
//            jedisClusterService.set(nhsRedisKeyPrefix + "_name", name, 1);
//            jedisClusterService.expire(nhsRedisKeyPrefix + "_name", 60, 1);
//            logger.info("set redis name : {}", name);
//        }
    }

    private void testRocketMQ() {
        rmqProducer.sendMsg("TestRocketMQ", "testContent", "Hello RocketMQ");
    }
}
