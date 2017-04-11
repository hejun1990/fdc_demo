package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.generation.ArticleMapper;
import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleExample;
import com.hejun.demo.service.inter.service.sitemanager.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public int countByExample(ArticleExample example) {
        return articleMapper.countByExample(example);
    }

    @Override
    public boolean insertSelective(Article record) {
        return articleMapper.insertSelective(record) > 0;
    }

    @Override
    public List<Article> selectByExample(ArticleExample example) {
        return articleMapper.selectByExample(example);
    }

    @Override
    public Article selectByPrimaryKey(String id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateByPrimaryKeySelective(Article record) {
        return articleMapper.updateByPrimaryKeySelective(record) > 0;
    }
}
