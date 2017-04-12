package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.ArticleTag;
import com.hejun.demo.service.inter.domain.generation.ArticleTagExample;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public interface ArticleTagService {
    boolean insert(ArticleTag record);

    List<ArticleTag> selectByExample(ArticleTagExample example);
}
