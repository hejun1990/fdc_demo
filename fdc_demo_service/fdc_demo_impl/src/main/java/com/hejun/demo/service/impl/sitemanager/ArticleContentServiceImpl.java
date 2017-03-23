package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.generation.ArticleContentMapper;
import com.hejun.demo.service.inter.domain.generation.ArticleContent;
import com.hejun.demo.service.inter.domain.generation.ArticleContentExample;
import com.hejun.demo.service.inter.service.sitemanager.ArticleContentService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/3/23.
 */
public class ArticleContentServiceImpl implements ArticleContentService {
    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Override
    public int countByExample(ArticleContentExample example) {
        return articleContentMapper.countByExample(example);
    }

    @Override
    public int insertSelective(ArticleContent record) {
        return articleContentMapper.insertSelective(record);
    }

    @Override
    public List<ArticleContent> selectByExampleWithBLOBs(ArticleContentExample example) {
        return articleContentMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public List<ArticleContent> selectByExample(ArticleContentExample example) {
        return articleContentMapper.selectByExample(example);
    }

    @Override
    public ArticleContent selectByPrimaryKey(String id) {
        return articleContentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByExampleSelective(ArticleContent record, ArticleContentExample example) {
        return articleContentMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(ArticleContent record) {
        return articleContentMapper.updateByPrimaryKeySelective(record);
    }
}
