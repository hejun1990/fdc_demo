package com.hejun.demo.web.controller;

import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.web.bussiness.ArticleBussiness;
import org.springframework.beans.factory.annotation.Autowired;
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
//    private Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @Autowired
    private ArticleBussiness articleBussiness;

    /**
     * 首页
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
     * @return
     */
    @RequestMapping(value = "/toaddarticle", method = RequestMethod.GET)
    public String toAddArticle() {
        return "/article/addArticle";
    }

    /**
     * 添加文章
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
}
