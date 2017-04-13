package com.hejun.demo.service.inter.domain.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdc.platform.common.validator.annotation.BizId;
import com.fdc.platform.common.validator.annotation.Domain;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Domain
@Data
@ToString
public class ArticleContent implements Serializable {
    private static final long serialVersionUID = -4624046995632549822L;
    /**
     * 主键
     */
    @BizId
    private String id;

    /**
     * 文章ID(外键,关联article表主键)
     */
    private String articleId;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 内容
     */
    private String content;

    /**
     * id属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String idExpression;

    /**
     * articleId属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String articleIdExpression;

    /**
     * summary属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String summaryExpression;

    /**
     * content属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String contentExpression;
}