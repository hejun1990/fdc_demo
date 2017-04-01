package com.hejun.demo.web.bussiness;

import com.hejun.demo.service.inter.domain.generation.Article;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/31.
 */
public interface ArticleBussiness {
    /**
     * 通过条件查询文章列表
     * @param params
     * @return
     */
    List<Article> getArticlesByPage(Map<String, Object> params);

    /**
     * 通过ID查询文章
     * @param id
     * @return
     */
    Article getArticleById(String id);

    /**
     * 通过条件查询文章
     * @param params
     * @return
     */
    Article getArticleByCondition(Map<String, Object> params);

    /**
     * 添加文章
     * @param article
     * @return
     */
    boolean addArticle(Article article);

    /**
     * 更新文章
     * @param article
     * @return
     */
    boolean updateArticle(Article article);

    /**
     * 删除文章
     * @param id
     * @return
     */
    boolean deleteArticleById(String id);
}
