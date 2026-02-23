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

import com.entity.HuzhuEntity;
import com.entity.UserEntity;
import com.entity.view.HuzhuView;

import com.service.HuzhuService;
import com.service.UserService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MD5Util;
import com.utils.MPUtil;
import com.utils.CommonUtil;


/**
 * 户主
 * 后端接口
 * @author 
 * @email 
 * @date 2021-03-13 12:56:24
 */
@RestController
@RequestMapping("/huzhu")
public class HuzhuController {
    @Autowired
    private HuzhuService huzhuService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录（统一 user 表，identity_type=2 为房东）
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        if (username == null || username.trim().isEmpty()) return R.error("请输入账号");
        if (password == null) password = "";
        UserEntity user = userService.selectOne(new EntityWrapper<UserEntity>()
                .eq("username", username.trim()).eq("identity_type", 2));
        if (user == null) return R.error("账号或密码不正确");
        String pwdDb = user.getPassword();
        if (pwdDb == null || !pwdDb.equals(MD5Util.md5(password))) {
            return R.error("账号或密码不正确");
        }
        String token = tokenService.generateToken(user.getId(), username, "huzhu", "户主");
        return R.ok().put("token", token);
    }

    /**
     * 注册（写入 user 表，identity_type=2）
     */
    @IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody HuzhuEntity huzhu) {
        UserEntity exist = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", huzhu.getZhanghao()));
        if (exist != null) {
            return R.error("注册用户已存在");
        }
        UserEntity user = new UserEntity();
        user.setUsername(huzhu.getZhanghao());
        user.setPassword(huzhu.getMima() != null ? MD5Util.md5(huzhu.getMima()) : "");
        user.setRealName(huzhu.getHuzhuxingming());
        user.setPhone(huzhu.getLianxifangshi());
        user.setEmail(huzhu.getYouxiang());
        user.setAddress(huzhu.getDizhi());
        user.setIdentityType(2);
        user.setStatus(1);
        // 默认头像（注册时）
        user.setAvatar("http://codegen.caihongy.cn/20201024/ed5e326ca66f403aa3197b5fbb4ec733.jpg");
        userService.insert(user);
        return R.ok();
    }

    /**
     * 退出
     */
    @RequestMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }

    /**
     * 获取当前登录用户信息（统一 user 表）
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute("userId");
        UserEntity user = userService.selectById(id);
        return R.ok().put("data", user);
    }

    /**
     * 密码重置
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        UserEntity user = userService.selectOne(new EntityWrapper<UserEntity>().eq("username", username).eq("identity_type", 2));
        if (user == null) {
            return R.error("账号不存在");
        }
        user.setPassword(MD5Util.md5("123456"));
        userService.updateById(user);
        return R.ok("密码已重置为：123456");
    }

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,HuzhuEntity huzhu,
		HttpServletRequest request){
        EntityWrapper<HuzhuEntity> ew = new EntityWrapper<HuzhuEntity>();
		PageUtils page = huzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huzhu), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,HuzhuEntity huzhu, HttpServletRequest request){
        EntityWrapper<HuzhuEntity> ew = new EntityWrapper<HuzhuEntity>();
		PageUtils page = huzhuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, huzhu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( HuzhuEntity huzhu){
       	EntityWrapper<HuzhuEntity> ew = new EntityWrapper<HuzhuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( huzhu, "huzhu")); 
        return R.ok().put("data", huzhuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(HuzhuEntity huzhu){
        EntityWrapper< HuzhuEntity> ew = new EntityWrapper< HuzhuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( huzhu, "huzhu")); 
		HuzhuView huzhuView =  huzhuService.selectView(ew);
		return R.ok("查询户主成功").put("data", huzhuView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        HuzhuEntity huzhu = huzhuService.selectById(id);
        return R.ok().put("data", huzhu);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        HuzhuEntity huzhu = huzhuService.selectById(id);
        return R.ok().put("data", huzhu);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody HuzhuEntity huzhu, HttpServletRequest request){
    	huzhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(huzhu);
        huzhuService.insert(huzhu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody HuzhuEntity huzhu, HttpServletRequest request){
    	huzhu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(huzhu);
        huzhuService.insert(huzhu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody HuzhuEntity huzhu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(huzhu);
        huzhuService.updateById(huzhu);//全部更新
        return R.ok();
    }
    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        huzhuService.deleteBatchIds(Arrays.asList(ids));
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
		
		Wrapper<HuzhuEntity> wrapper = new EntityWrapper<HuzhuEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}


		int count = huzhuService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	


}
