package com.hejun.demo.service.impl.sitemanager;

import com.hejun.demo.service.inter.dao.generation.WebsiteSpiderMapper;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import com.hejun.demo.service.inter.service.sitemanager.WebsiteSpiderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public class WebsiteSpiderServiceImpl implements WebsiteSpiderService {
    @Autowired
    private WebsiteSpiderMapper websiteSpiderMapper;
    @Override
    public int countByExample(WebsiteSpiderExample example) {
        return websiteSpiderMapper.countByExample(example);
    }

    @Override
    public boolean insertSelective(WebsiteSpider record) {
        return websiteSpiderMapper.insertSelective(record) > 0;
    }

    @Override
    public WebsiteSpider selectByPrimaryKey(String id) {
        return websiteSpiderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<WebsiteSpider> selectByExample(WebsiteSpiderExample example) {
        return websiteSpiderMapper.selectByExample(example);
    }

    @Override
    public boolean updateByPrimaryKeySelective(@Param("record") WebsiteSpider record) {
        return websiteSpiderMapper.updateByPrimaryKeySelective(record) > 0;
    }
}
