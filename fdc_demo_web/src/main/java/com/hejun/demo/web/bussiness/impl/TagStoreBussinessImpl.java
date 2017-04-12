package com.hejun.demo.web.bussiness.impl;

import com.hejun.demo.service.inter.domain.generation.TagStore;
import com.hejun.demo.service.inter.domain.generation.TagStoreExample;
import com.hejun.demo.service.inter.service.sitemanager.TagStoreService;
import com.hejun.demo.web.bussiness.TagStoreBussiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hejun-FDC on 2017/4/12.
 */
@Service("tagStoreBussiness")
public class TagStoreBussinessImpl implements TagStoreBussiness {
    @Autowired
    private TagStoreService tagStoreService;

    @Override
    public boolean addTagStore(TagStore record) {
        return tagStoreService.insertSelective(record);
    }

    @Override
    public List<TagStore> selectByExample(TagStoreExample example) {
        return tagStoreService.selectByExample(example);
    }
}
