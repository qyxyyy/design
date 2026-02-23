package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.LuntanDao;
import com.entity.LuntanEntity;
import com.service.LuntanService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("luntanService")
public class LuntanServiceImpl extends ServiceImpl<LuntanDao, LuntanEntity> implements LuntanService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        return queryPage(params, new EntityWrapper<LuntanEntity>().orderBy("addtime", false));
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<LuntanEntity> wrapper) {
        Page<LuntanEntity> page = this.selectPage(new Query<LuntanEntity>(params).getPage(), wrapper);
        return new PageUtils(page);
    }
}
