package com.hejun.demo.service.inter.domain.defined;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by hejun-FDC on 2017/4/20.
 */
@Data
@ToString
public class Paging implements Serializable {
    private static final long serialVersionUID = -2230396855036594762L;
    /**
     * 起始页
     */
    private Integer start;

    /**
     * 条数
     */
    private Integer pageSize;

    /**
     * 总条数
     */
    private Integer totleCount;

    /**
     * 总页数
     */
    private Integer totlePage;

    public Paging() {
        this.start = 0;
        this.pageSize = 10;
    }

    public Paging(int start) {
        if (start > 0) {
            this.start = start - 1;
        } else {
            this.start = 0;
        }
        this.pageSize = 10;
    }

    public Paging(int start, int pageSize) {
        if (start > 0) {
            this.start = start - 1;
        } else {
            this.start = 0;
        }
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 10;
        }
    }

    public Paging(int start, int pageSize, int totleCount) {
        if (start > 0) {
            this.start = start - 1;
        } else {
            this.start = 0;
        }
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 10;
        }
        if (totleCount < 0) {
            this.totleCount = 0;
        } else {
            this.totleCount = totleCount;
        }
        calculateTotlePage();
    }

    public int calculateTotlePage() {
        if (pageSize != null && pageSize > 0 && totleCount != null && totleCount > 0) {
            totlePage = totleCount % pageSize == 0 ? totleCount / pageSize : totleCount / pageSize + 1;
        }
        return totlePage == null ? 0 : totlePage;
    }
}
