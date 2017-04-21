package com.hejun.demo.service.inter.domain.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;

import com.hejun.demo.service.inter.validator.annotation.BizId;
import com.hejun.demo.service.inter.validator.annotation.Domain;
import lombok.Data;
import lombok.ToString;

@Domain
@Data
@ToString
public class Article implements Serializable {
    private static final long serialVersionUID = 253422878200987511L;
    /**
     * 主键
     */
    @BizId
    private String id;

    /**
     * 创建时间
     */
    private Date gmtCreated;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 修改人
     */
    private String modifiedBy;

    /**
     * 是否删除(0:存在;1:删除)
     */
    private int isDel;

    /**
     * 文章类型
     */
    private Integer articleType;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String viceTitle;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源网站编号
     */
    private Integer originalSiteCode;

    /**
     * 来源网站名称
     */
    private String originalSiteName;

    /**
     * 来源链接
     */
    private String originalUrl;

    /**
     * 图片
     */
    private String picUrl;

    /**
     * 关键字(多个标签以逗号分隔)
     */
    private String keywords;

    /**
     * 发布时间
     */
    private Date pubTime;

    /**
     * 阅读次数
     */
    private Integer readCount;

    /**
     * 喜欢次数
     */
    private Integer likeCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * id属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String idExpression;

    /**
     * gmtCreated属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String gmtCreatedExpression;

    /**
     * gmtModified属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String gmtModifiedExpression;

    /**
     * createdBy属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String createdByExpression;

    /**
     * modifiedBy属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String modifiedByExpression;

    /**
     * isDel属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String isDelExpression;

    /**
     * articleType属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String articleTypeExpression;

    /**
     * title属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String titleExpression;

    /**
     * viceTitle属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String viceTitleExpression;

    /**
     * author属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String authorExpression;

    /**
     * originalSiteCode属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalSiteCodeExpression;

    /**
     * originalSiteName属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalSiteNameExpression;

    /**
     * originalUrl属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalUrlExpression;

    /**
     * picUrl属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String picUrlExpression;

    /**
     * keywords属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String keywordsExpression;

    /**
     * pubTime属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String pubTimeExpression;

    /**
     * readCount属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String readCountExpression;

    /**
     * likeCount属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String likeCountExpression;

    /**
     * commentCount属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String commentCountExpression;

    /**
     * version属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String versionExpression;
}