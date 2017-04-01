package com.hejun.demo.web.bussiness.impl;

import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.web.bussiness.ArticleBussiness;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hejun-FDC on 2017/3/31.
 */
@Service("articleBussiness")
public class ArticleBussinessImpl implements ArticleBussiness {
    @Override
    public List<Article> getArticlesByPage(Map<String, Object> params) {
        return null;
    }

    @Override
    public Article getArticleById(String id) {
        return null;
    }

    @Override
    public Article getArticleByCondition(Map<String, Object> params) {
        return null;
    }

    @Override
    public boolean addArticle(Article article) {
        return false;
    }

    @Override
    public boolean updateArticle(Article article) {
        return false;
    }

    @Override
    public boolean deleteArticleById(String id) {
        return false;
    }
}
