package com.hejun.demo.web.bussiness;

import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
public interface TagStoreBussiness {
    boolean addTagStore(TagStore record);
}
