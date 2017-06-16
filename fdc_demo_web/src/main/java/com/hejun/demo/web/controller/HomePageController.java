package com.hejun.demo.web.controller;

import com.hejun.demo.service.inter.cached.JedisClusterService;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.web.bussiness.ArticleBussiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/24.
 */
@Controller
public class HomePageController {
    private static Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @Value("${redis_key_prefix}")
    private String nhsRedisKeyPrefix;

    @Autowired
    private JedisClusterService jedisClusterService;

    @Autowired
    private ArticleBussiness articleBussiness;

    /**
     * 首页
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap) {
        modelMap.put("content", "Hello My Friend");
        return "home";
    }

    /**
     * 进入添加文章页面
     *
     * @return
     */
    @RequestMapping(value = "/toaddarticle", method = RequestMethod.GET)
    public String toAddArticle() {
        return "/article/addArticle";
    }

    /**
     * 添加文章
     *
     * @param article
     * @return
     */
    @RequestMapping(value = "/addarticle")
    public
    @ResponseBody
    Map<String, Object> addArticle(Article article) {
        Map<String, Object> result = new HashMap<>();
        boolean data = articleBussiness.addArticle(article);
        result.put("data", data);
        return result;
    }

    /**
     * 后台管理系统首页
     *
     * @return vm模板页
     */
    @RequestMapping(value = "/backend", method = RequestMethod.GET)
    public String home() {
        if (jedisClusterService.exists(nhsRedisKeyPrefix + "_name", 1)) {
            String name = jedisClusterService.get(nhsRedisKeyPrefix + "_name", 1);
            logger.info("get redis name : {}", name);
        } else {
            logger.info("get redis name : 空");
        }
        return "backend/home";
    }
}
