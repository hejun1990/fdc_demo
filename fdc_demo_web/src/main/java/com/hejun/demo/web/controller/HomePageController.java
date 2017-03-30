package com.hejun.demo.web.controller;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/24.
 */
@Controller
public class HomePageController {
    private Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap) {
        modelMap.put("content", "Hello My Friend");
        return "home";
    }

    @RequestMapping(value = "/getView")
    public
    @ResponseBody
    Map<String, Object> getView(@RequestParam(value = "id", required = false) int id) {
        logger.info("id = {}", id);
        id++;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("view", "This is My View");
        return map;
    }

    @RequestMapping(value = "/error404", method = RequestMethod.GET)
    public String error404(ModelMap modelMap) {
        modelMap.put("headerName", "error");
        return "error404";
    }
}
