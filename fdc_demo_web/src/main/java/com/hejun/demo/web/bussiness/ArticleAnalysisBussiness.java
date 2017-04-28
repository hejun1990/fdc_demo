package com.hejun.demo.web.bussiness;

/**
 * Created by hejun-FDC on 2017/4/17.
 */
public interface ArticleAnalysisBussiness {
    /**
     * 提取网页正文
     *
     * @param begin 起始页
     */
    void extractWebContent(int begin);
}
