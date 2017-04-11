package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.generation.ArticleTagMapper;
import com.hejun.demo.service.inter.domain.generation.ArticleTag;
import com.hejun.demo.service.inter.service.sitemanager.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public class ArticleTagServiceImpl implements ArticleTagService {
    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public boolean insert(ArticleTag record) {
        return articleTagMapper.insert(record) > 0;
    }
}
