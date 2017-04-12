package com.hejun.demo.web.bussiness.impl;

import com.hejun.demo.service.inter.domain.generation.ArticleTag;
import com.hejun.demo.service.inter.service.sitemanager.ArticleTagService;
import com.hejun.demo.web.bussiness.ArticleTagBussiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
@Service("articleTagBussiness")
public class ArticleTagBussinessImpl implements ArticleTagBussiness {
    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public boolean addArticleTag(ArticleTag articleTag) {
        return articleTagService.insert(articleTag);
    }
}
