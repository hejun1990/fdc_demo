package com.hejun.demo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by hejun-FDC on 2017/3/24.
 */
@Controller
public class HomePageController {
    @RequestMapping(value = "/error404", method = RequestMethod.GET)
    public ModelAndView error404(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("headerName","error");
        mv.setViewName("error404");
        return mv;
    }
}
