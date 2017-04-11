package com.hejun.demo.service.inter.domain.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdc.platform.common.validator.annotation.Domain;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Domain
@Data
@ToString
public class ArticleTag implements Serializable {
    /** 文章ID(外键,关联article表主键) */
    private String articleId;

    /** 标签ID(外键,关联tag_store表主键) */
    private String tagId;

    /** articleId属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String articleIdExpression;

    /** tagId属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String tagIdExpression;

    private static final long serialVersionUID = 1L;
}