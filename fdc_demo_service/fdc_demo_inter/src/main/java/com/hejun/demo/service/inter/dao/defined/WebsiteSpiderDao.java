package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.generation.WebsiteSpider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WebsiteSpiderDao {
    int countByCondition(Map<String, Object> condition);

    List<WebsiteSpider> selectPageByCondition(Map<String, Object> condition);
}