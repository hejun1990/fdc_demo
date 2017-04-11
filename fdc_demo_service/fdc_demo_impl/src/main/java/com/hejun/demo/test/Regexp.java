package com.hejun.demo.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hejun-FDC on 2017/4/10.
 */
public class Regexp {
    public static void main(String[] args) throws IOException {
        String content = "<div><a href=\"https://www.baidu.com\" title=\"百度\">baidu_123</a></div>";
        content += "<div><a title=\"搜狐\" href=\"//www.sohu.com/a/133050631_385655\" >souhu_456</a><a title=\"新浪\" href='https://www.sina.cn' >sina_789</a></div>";
        content += "<span><a href=\"http://www.163.com?id=111&ucd=hj\">163_163</a></span>";
        content += "<span><a id='fenghuang' v-model=\"param.url\" href='https://www.ifeng.com' name='ifeng.com'>ifeng_666</a></span>";
        String pattern = "<a[^>]*href=(?:'|\")((?:http:|https:|//)[^>'\"]+)[^>]*>(.*?)</a>";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        while (m.find()) {
            System.out.println("find......");
            System.out.println("url=" + m.group(1) + ", text=" + m.group(2));
        }
    }
}
