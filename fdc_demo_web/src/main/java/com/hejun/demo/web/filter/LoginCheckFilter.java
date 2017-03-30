package com.hejun.demo.web.filter;

import com.fdc.platform.common.yfutil.PropertyReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

public class LoginCheckFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestMethod = httpRequest.getMethod();
        String requestParamStr = httpRequest.getQueryString();
        String thisURI = httpRequest.getRequestURI();
        // 校验是否有特殊字符 “跨站攻击” （不包含“搜索页”）
        if ("GET".equals(requestMethod) && StringUtils.isNotEmpty(requestParamStr) && !"/search/".equals(thisURI)) {
            Enumeration<String> params_enum = servletRequest.getParameterNames();
            boolean checkCharactorFlag = true;
            StringBuilder new_param_sb = new StringBuilder();
            while (params_enum.hasMoreElements()) {
                String thisParamName = params_enum.nextElement();
                String thisParamValue = servletRequest.getParameter(thisParamName);
                if (StringUtils.isNotEmpty(thisParamName)) {
                    if (thisParamName.indexOf("<") > -1 || thisParamName.indexOf(">") > -1) {
                        thisParamName = thisParamName.replaceAll("<", "");
                        thisParamName = thisParamName.replaceAll(">", "");
                        checkCharactorFlag = false;
                    }
                }
                if (StringUtils.isNotEmpty(thisParamValue)) {
                    if (thisParamValue.indexOf("<") > -1 || thisParamValue.indexOf(">") > -1) {
                        thisParamValue = thisParamValue.replaceAll("<", "");
                        thisParamValue = thisParamValue.replaceAll(">", "");
                        checkCharactorFlag = false;
                    }
                }
                new_param_sb.append(thisParamName).append("=").append(thisParamValue).append("&");
            }
            if (checkCharactorFlag == false) {
                String new_param_str = new_param_sb.toString();
                new_param_str = new_param_str.substring(0, new_param_str.length() - 1);
                String newRequestURL = thisURI + "?" + new_param_str;
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendRedirect(newRequestURL);
                return;
            }
        }

        HttpSession session = httpRequest.getSession();
        // 静态文件引用的服务器地址（项目中的css和js文件）
        if(session.getAttribute("static_server_url") == null) {
            String static_server_url = PropertyReader.getValue("static_server_url");
            if (StringUtils.isNotEmpty(static_server_url)) {
                session.setAttribute("static_server_url", static_server_url);
            }
        }
        if(session.getAttribute("web_project") == null) {
            String web_project = PropertyReader.getValue("web_project");
            if (StringUtils.isNotEmpty(web_project)) {
                session.setAttribute("web_project", web_project);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}

