package com.hejun.demo.web.enumeration;

/**
 * Created by hejun-FDC on 2017/4/20.
 */
public enum ArticleType {
    TECHNOLOGY(1, "科技");

    /**
     * 类型编号
     */
    private int code;

    /**
     * 类型名
     */
    private String name;

    ArticleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
