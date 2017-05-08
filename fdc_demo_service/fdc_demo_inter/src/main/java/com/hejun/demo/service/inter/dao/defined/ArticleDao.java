package com.hejun.demo.service.inter.dao.defined;

import com.hejun.demo.service.inter.domain.defined.Paging;
import com.hejun.demo.service.inter.domain.generation.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ArticleDao {
    int countByCondition(@Param("record") Map<String, Object> record);

    List<Article> selectPageByCondition(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);

    int updateByConditionSelective(@Param("record") Article record, @Param("example") Article example);

    List<Article> selectPageByConditionNoOrder(@Param("record") Map<String, Object> record, @Param("paging") Paging paging);
}