package com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.FangwuxinxiEntity;
import com.entity.view.FangwuxinxiView;
import com.entity.FangwuzhengmingEntity;
import com.entity.UserEntity;

import com.service.FangwuxinxiService;
import com.service.FangwuzhengmingService;
import com.service.TokenService;
import com.service.UserService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 房屋信息
 * 后端接口
 * @author 
 * @email 
 * @date 2021-03-13 12:56:24
 */
@RestController
@RequestMapping("/fangwuxinxi")
public class FangwuxinxiController {
    @Autowired
    private FangwuxinxiService fangwuxinxiService;
    @Autowired
    private FangwuzhengmingService fangwuzhengmingService;
    @Autowired
    private UserService userService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,FangwuxinxiEntity fangwuxinxi,
		HttpServletRequest request){
        EntityWrapper<FangwuxinxiEntity> ew = new EntityWrapper<FangwuxinxiEntity>();
		PageUtils page = fangwuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, fangwuxinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,FangwuxinxiEntity fangwuxinxi, HttpServletRequest request){
        EntityWrapper<FangwuxinxiEntity> ew = new EntityWrapper<FangwuxinxiEntity>();
        if ("已通过".equals(params.get("shenheZhuangtai"))) {
            ew.eq("shenhe_zhuangtai", "已通过");
        }
        if ("1".equals(params.get("myList")) && "huzhu".equals(request.getSession().getAttribute("tableName"))) {
            Long uid = (Long) request.getSession().getAttribute("userId");
            if (uid != null) {
                UserEntity u = userService.selectById(uid);
                if (u != null && u.getRealName() != null && !u.getRealName().trim().isEmpty()) {
                    ew.eq("huzhuxingming", u.getRealName().trim());
                }
            }
        }
		PageUtils page = fangwuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, fangwuxinxi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( FangwuxinxiEntity fangwuxinxi){
       	EntityWrapper<FangwuxinxiEntity> ew = new EntityWrapper<FangwuxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( fangwuxinxi, "fangwuxinxi")); 
        return R.ok().put("data", fangwuxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(FangwuxinxiEntity fangwuxinxi){
        EntityWrapper< FangwuxinxiEntity> ew = new EntityWrapper< FangwuxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( fangwuxinxi, "fangwuxinxi")); 
		FangwuxinxiView fangwuxinxiView =  fangwuxinxiService.selectView(ew);
		return R.ok("查询房屋信息成功").put("data", fangwuxinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        FangwuxinxiEntity fangwuxinxi = fangwuxinxiService.selectById(id);
		fangwuxinxi.setClicknum(fangwuxinxi.getClicknum()+1);
		fangwuxinxi.setClicktime(new Date());
		fangwuxinxiService.updateById(fangwuxinxi);
        return R.ok().put("data", fangwuxinxi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        FangwuxinxiEntity fangwuxinxi = fangwuxinxiService.selectById(id);
		fangwuxinxi.setClicknum(fangwuxinxi.getClicknum()+1);
		fangwuxinxi.setClicktime(new Date());
		fangwuxinxiService.updateById(fangwuxinxi);
        return R.ok().put("data", fangwuxinxi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody FangwuxinxiEntity fangwuxinxi, HttpServletRequest request){
    	fangwuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(fangwuxinxi);
        fangwuxinxiService.insert(fangwuxinxi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody FangwuxinxiEntity fangwuxinxi, HttpServletRequest request){
    	fangwuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	fangwuxinxi.setShenheZhuangtai("待提交证明");
    	if (fangwuxinxi.getZhuangtai() == null || fangwuxinxi.getZhuangtai().isEmpty()) {
    	    fangwuxinxi.setZhuangtai("待租");
    	}
    	//ValidatorUtils.validateEntity(fangwuxinxi);
        fangwuxinxiService.insert(fangwuxinxi);
        Long huzhuId = (Long) request.getSession().getAttribute("userId");
        if (huzhuId != null) {
            FangwuzhengmingEntity task = new FangwuzhengmingEntity();
            task.setFangwuId(fangwuxinxi.getId());
            task.setHuzhuId(huzhuId);
            task.setStatus("待提交");
            task.setZhengmingFiles("");
            task.setCreateTime(new Date());
            task.setUpdateTime(new Date());
            fangwuzhengmingService.insert(task);
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody FangwuxinxiEntity fangwuxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(fangwuxinxi);
        fangwuxinxiService.updateById(fangwuxinxi);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        fangwuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<FangwuxinxiEntity> wrapper = new EntityWrapper<FangwuxinxiEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = fangwuxinxiService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
	/**
     * 前端智能排序
     */
	@IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params,FangwuxinxiEntity fangwuxinxi, HttpServletRequest request,String pre){
        EntityWrapper<FangwuxinxiEntity> ew = new EntityWrapper<FangwuxinxiEntity>();
        ew.eq("shenhe_zhuangtai", "已通过");
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = entry.getKey();
			if (pre != null && pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else if (pre != null) {
				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		params.put("sort", "clicknum");
        params.put("order", "desc");
		PageUtils page = fangwuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, fangwuxinxi), params), params));
        return R.ok().put("data", page);
    }


}
