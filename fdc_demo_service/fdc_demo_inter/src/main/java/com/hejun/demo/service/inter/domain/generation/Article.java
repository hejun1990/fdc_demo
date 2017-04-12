package com.hejun.demo.service.inter.domain.generation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fdc.platform.common.validator.annotation.BizId;
import com.fdc.platform.common.validator.annotation.Domain;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Domain
@Data
@ToString
public class Article implements Serializable {
    private static final long serialVersionUID = -5199743972285405667L;
    /** 主键 */
    @BizId
    private String id;

    /** 创建时间 */
    private Date gmtCreated;

    /** 修改时间 */
    private Date gmtModified;

    /** 创建人 */
    private String createdBy;

    /** 修改人 */
    private String modifiedBy;

    /** 是否删除(0:存在;1:删除) */
    private int isDel;

    /** 文章类型 */
    private Byte articleType;

    /** 标题 */
    private String title;

    /** 副标题 */
    private String viceTitle;

    /** 作者 */
    private String author;

    /** 来源网站编号 */
    private Byte originalSiteCode;

    /** 来源网站名称 */
    private String originalSiteName;

    /** 来源链接 */
    private String originalUrl;

    /** 图片 */
    private String picUrl;

    /** id属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String idExpression;

    /** gmtCreated属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String gmtCreatedExpression;

    /** gmtModified属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String gmtModifiedExpression;

    /** createdBy属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String createdByExpression;

    /** modifiedBy属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String modifiedByExpression;

    /** isDel属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String isDelExpression;

    /** articleType属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String articleTypeExpression;

    /** title属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String titleExpression;

    /** viceTitle属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String viceTitleExpression;

    /** author属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String authorExpression;

    /** originalSiteCode属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalSiteCodeExpression;

    /** originalSiteName属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalSiteNameExpression;

    /** originalUrl属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String originalUrlExpression;

    /** picUrl属性对应的表达式属性，
     * 目前用来支持update set 字段=表达式(如:'字段+1')这种场景
     **/
    @JsonIgnore
    private String picUrlExpression;
}