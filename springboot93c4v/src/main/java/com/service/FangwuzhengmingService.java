package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.FangwuzhengmingEntity;
import com.utils.PageUtils;

import java.util.Map;

/**
 * 房屋证明资料任务
 */
public interface FangwuzhengmingService extends IService<FangwuzhengmingEntity> {

    PageUtils queryPage(Map<String, Object> params, Wrapper<FangwuzhengmingEntity> wrapper);
}
