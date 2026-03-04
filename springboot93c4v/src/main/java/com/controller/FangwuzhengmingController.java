package com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.FangwuxinxiEntity;
import com.entity.FangwuzhengmingEntity;
import com.service.FangwuxinxiService;
import com.service.FangwuzhengmingService;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 房屋证明资料任务（户主提交证明、管理员审核）
 */
@RestController
@RequestMapping("/fangwuzhengming")
public class FangwuzhengmingController {

    @Autowired
    private FangwuzhengmingService fangwuzhengmingService;
    @Autowired
    private FangwuxinxiService fangwuxinxiService;

    /**
     * 管理员：证明任务分页列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Object tableName = request.getSession().getAttribute("tableName");
        if (tableName == null || !"users".equals(tableName.toString())) {
            return R.error(403, "仅管理员可操作");
        }
        EntityWrapper<FangwuzhengmingEntity> ew = new EntityWrapper<>();
        // 按状态筛选（可选）
        if (params.get("status") != null && !"".equals(params.get("status"))) {
            ew.eq("status", params.get("status"));
        }
        ew.orderBy("create_time", false);
        PageUtils page = fangwuzhengmingService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 户主：我的证明待办列表（带房屋编号、类型等简要信息）
     */
    @RequestMapping("/myTasks")
    public R myTasks(HttpServletRequest request) {
        Long huzhuId = (Long) request.getSession().getAttribute("userId");
        if (huzhuId == null) {
            return R.error(401, "请先登录");
        }
        EntityWrapper<FangwuzhengmingEntity> ew = new EntityWrapper<>();
        ew.eq("huzhu_id", huzhuId).orderBy("create_time", false);
        List<FangwuzhengmingEntity> list = fangwuzhengmingService.selectList(ew);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (FangwuzhengmingEntity task : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", task.getId());
            item.put("fangwuId", task.getFangwuId());
            item.put("status", task.getStatus());
            item.put("shenheBeizhu", task.getShenheBeizhu());
            item.put("zhengmingFiles", task.getZhengmingFiles());
            item.put("createTime", task.getCreateTime());
            item.put("updateTime", task.getUpdateTime());
            FangwuxinxiEntity fangwu = fangwuxinxiService.selectById(task.getFangwuId());
            if (fangwu != null) {
                item.put("fangwubianhao", fangwu.getFangwubianhao());
                item.put("fangyuanleixing", fangwu.getFangyuanleixing());
                item.put("shenheZhuangtai", fangwu.getShenheZhuangtai());
            } else {
                item.put("fangwubianhao", "");
                item.put("fangyuanleixing", "");
                item.put("shenheZhuangtai", "");
            }
            result.add(item);
        }
        return R.ok().put("data", result);
    }

    /**
     * 户主：根据房屋ID获取该房屋的证明任务（用于提交/编辑证明）
     */
    @RequestMapping("/getByFangwuId")
    public R getByFangwuId(@RequestParam Long fangwuId, HttpServletRequest request) {
        Long huzhuId = (Long) request.getSession().getAttribute("userId");
        if (huzhuId == null) {
            return R.error(401, "请先登录");
        }
        EntityWrapper<FangwuzhengmingEntity> ew = new EntityWrapper<>();
        ew.eq("fangwu_id", fangwuId).eq("huzhu_id", huzhuId);
        FangwuzhengmingEntity one = fangwuzhengmingService.selectOne(ew);
        return R.ok().put("data", one);
    }

    /**
     * 户主：提交/更新证明资料，状态改为待审核，并同步房屋审核状态
     */
    @RequestMapping("/submitProof")
    public R submitProof(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long huzhuId = (Long) request.getSession().getAttribute("userId");
        if (huzhuId == null) {
            return R.error(401, "请先登录");
        }
        Object fangwuIdObj = params.get("fangwuId");
        String zhengmingFiles = params.get("zhengmingFiles") != null ? params.get("zhengmingFiles").toString().trim() : "";
        if (fangwuIdObj == null) {
            return R.error("缺少房屋ID");
        }
        Long fangwuId = Long.valueOf(fangwuIdObj.toString());
        if (StringUtils.isEmpty(zhengmingFiles)) {
            return R.error("请至少上传一份证明资料");
        }
        FangwuxinxiEntity fangwu = fangwuxinxiService.selectById(fangwuId);
        if (fangwu == null) {
            return R.error("房屋不存在");
        }
        EntityWrapper<FangwuzhengmingEntity> ew = new EntityWrapper<>();
        ew.eq("fangwu_id", fangwuId).eq("huzhu_id", huzhuId);
        FangwuzhengmingEntity task = fangwuzhengmingService.selectOne(ew);
        if (task == null) {
            task = new FangwuzhengmingEntity();
            task.setFangwuId(fangwuId);
            task.setHuzhuId(huzhuId);
            task.setCreateTime(new Date());
        }
        task.setZhengmingFiles(zhengmingFiles);
        task.setStatus("待审核");
        task.setShenheBeizhu("");
        task.setUpdateTime(new Date());
        if (task.getId() == null) {
            fangwuzhengmingService.insert(task);
        } else {
            fangwuzhengmingService.updateById(task);
        }

        fangwu.setShenheZhuangtai("待审核");
        fangwu.setShenheBeizhu("");
        fangwuxinxiService.updateById(fangwu);
        return R.ok("提交成功，请等待管理员审核");
    }

    /**
     * 详情（户主查看自己的任务）
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        Long huzhuId = (Long) request.getSession().getAttribute("userId");
        if (huzhuId == null) {
            return R.error(401, "请先登录");
        }
        FangwuzhengmingEntity task = fangwuzhengmingService.selectById(id);
        if (task == null || !task.getHuzhuId().equals(huzhuId)) {
            return R.error("记录不存在或无权查看");
        }
        return R.ok().put("data", task);
    }

    /**
     * 管理员：审核（已通过/已驳回）
     */
    @RequestMapping("/audit")
    public R audit(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String tableName = request.getSession().getAttribute("tableName") != null
            ? request.getSession().getAttribute("tableName").toString() : "";
        if (!"users".equals(tableName)) {
            return R.error(403, "仅管理员可操作");
        }
        Object idObj = params.get("id");
        String status = params.get("status") != null ? params.get("status").toString().trim() : "";
        String shenheBeizhu = params.get("shenheBeizhu") != null ? params.get("shenheBeizhu").toString().trim() : "";
        if (idObj == null) {
            return R.error("缺少任务ID");
        }
        if (!"已通过".equals(status) && !"已驳回".equals(status)) {
            return R.error("状态须为：已通过 或 已驳回");
        }
        Long id = Long.valueOf(idObj.toString());
        FangwuzhengmingEntity task = fangwuzhengmingService.selectById(id);
        if (task == null) {
            return R.error("任务不存在");
        }
        task.setStatus(status);
        task.setShenheBeizhu(shenheBeizhu);
        task.setUpdateTime(new Date());
        fangwuzhengmingService.updateById(task);

        FangwuxinxiEntity fangwu = fangwuxinxiService.selectById(task.getFangwuId());
        if (fangwu != null) {
            fangwu.setShenheZhuangtai(status);
            fangwu.setShenheBeizhu(shenheBeizhu);
            fangwuxinxiService.updateById(fangwu);
        }
        return R.ok("审核完成");
    }
}
