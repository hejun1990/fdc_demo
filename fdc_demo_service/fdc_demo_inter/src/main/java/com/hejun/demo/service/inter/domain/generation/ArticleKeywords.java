package com.hejun.demo.service.inter.domain.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ArticleKeywords implements Serializable {
    private static final long serialVersionUID = 7133440085002049745L;
    /**
     * 文章ID(外键,关联article表主键)
     */
    private String articleId;

    /**
     * 关键字ID(外键,关联keywords_store表主键)
     */
    private String keywordsId;

    /**
     * articleId属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String articleIdExpression;

    /**
     * keywordsId属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String keywordsIdExpression;
}