package com.hk.service.common;

import com.hk.dto.count.TagCount;
import com.hk.service.count.CountService;
import com.hk.utils.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kang on 2019/1/14.
 */
@Service
public class RenderService {
    @Autowired
    private CountService countService;

    public TagCount renderTagCount(long tagId) {
        return BeanUtils.copy(countService.getTagCount(tagId), TagCount.class);
    }
}
