package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import com.hejun.demo.service.inter.domain.generation.WebsiteSpiderExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WebsiteSpiderDao {
    int countByCondition(@Param("record") Map<String, Object> record);

    List<WebsiteSpider> selectPageByCondition(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);

    int updateByConditionSelective(@Param("record") WebsiteSpider record, @Param("example") WebsiteSpider example);

    List<WebsiteSpider> selectPageByConditionNoOrder(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);
}