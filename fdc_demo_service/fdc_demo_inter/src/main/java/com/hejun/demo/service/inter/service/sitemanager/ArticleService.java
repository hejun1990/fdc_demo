package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.Article;
import com.hejun.demo.service.inter.domain.generation.ArticleExample;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public interface ArticleService {
    int countByExample(ArticleExample example);

    int insertSelective(Article record);

    List<Article> selectByExample(ArticleExample example);

    Article selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Article record);

}
