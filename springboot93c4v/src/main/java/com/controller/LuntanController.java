package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.LuntanEntity;
import com.service.LuntanService;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 论坛
 */
@RestController
@RequestMapping("/luntan")
public class LuntanController {

    @Autowired
    private LuntanService luntanService;

    /** 前端列表（所有人可看） */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, LuntanEntity luntan) {
        EntityWrapper<LuntanEntity> ew = new EntityWrapper<>();
        if (luntan != null && luntan.getTitle() != null && !luntan.getTitle().trim().isEmpty()) {
            ew.like("title", luntan.getTitle());
        }
        ew.orderBy("addtime", false);
        PageUtils page = luntanService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /** 前端详情 */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        LuntanEntity luntan = luntanService.selectById(id);
        return R.ok().put("data", luntan);
    }

    /** 发帖（需登录） */
    @RequestMapping("/add")
    public R add(@RequestBody LuntanEntity luntan, HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        String username = (String) request.getSession().getAttribute("username");
        if (userId == null) {
            return R.error("请先登录");
        }
        luntan.setUserid(userId);
        luntan.setUsername(username != null ? username : "");
        luntan.setAddtime(new Date());
        luntanService.insert(luntan);
        return R.ok();
    }
}
