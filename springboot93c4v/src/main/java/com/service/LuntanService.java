package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.LuntanEntity;
import com.utils.PageUtils;

import java.util.Map;

/**
 * 论坛
 */
public interface LuntanService extends IService<LuntanEntity> {
    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params, Wrapper<LuntanEntity> wrapper);
}
