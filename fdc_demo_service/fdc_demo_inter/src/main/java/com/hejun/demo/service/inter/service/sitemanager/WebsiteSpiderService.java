package com.hejun.demo.service.inter.service.sitemanager;

import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/11.
 */
public interface WebsiteSpiderService {
    int countByExample(WebsiteSpiderExample example);

    boolean insertSelective(WebsiteSpider record);

    WebsiteSpider selectByPrimaryKey(String id);

    List<WebsiteSpider> selectByExample(WebsiteSpiderExample example);

    boolean updateByPrimaryKeySelective(@Param("record") WebsiteSpider record);
}
