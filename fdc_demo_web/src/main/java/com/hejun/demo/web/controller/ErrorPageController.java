package com.hejun.demo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by hejun-FDC on 2017/3/31.
 */
@Controller
public class ErrorPageController {
    @RequestMapping(value = "/error404", method = RequestMethod.GET)
    public String error404(ModelMap modelMap) {
        modelMap.put("headerName", "error");
        return "error404";
    }
}
