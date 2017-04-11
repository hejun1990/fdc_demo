package com.hejun.demo.service.inter.dao.generation;

import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteSpiderMapper {
    int countByExample(WebsiteSpiderExample example);

    int deleteByExample(WebsiteSpiderExample example);

    int deleteByPrimaryKey(String id);

    int insert(WebsiteSpider record);

    int insertSelective(WebsiteSpider record);

    List<WebsiteSpider> selectByExample(WebsiteSpiderExample example);

    WebsiteSpider selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") WebsiteSpider record, @Param("example") WebsiteSpiderExample example);

    int updateByExample(@Param("record") WebsiteSpider record, @Param("example") WebsiteSpiderExample example);

    int updateByPrimaryKeySelective(@Param("record") WebsiteSpider record);

    int updateByPrimaryKey(WebsiteSpider record);
}