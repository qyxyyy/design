package com.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.FangwuzhengmingDao;
import com.entity.FangwuzhengmingEntity;
import com.service.FangwuzhengmingService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("fangwuzhengmingService")
public class FangwuzhengmingServiceImpl extends ServiceImpl<FangwuzhengmingDao, FangwuzhengmingEntity> implements FangwuzhengmingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<FangwuzhengmingEntity> wrapper) {
        Page<FangwuzhengmingEntity> page = this.selectPage(
                new Query<FangwuzhengmingEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }
}
