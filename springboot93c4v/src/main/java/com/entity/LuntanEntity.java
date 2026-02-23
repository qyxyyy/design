package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 论坛
 */
@TableName("luntan")
public class LuntanEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId
	private Long id;
	/** 标题 */
	private String title;
	/** 内容 */
	private String content;
	/** 发帖人id */
	private Long userid;
	/** 发帖人 */
	private String username;
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	public Long getUserid() { return userid; }
	public void setUserid(Long userid) { this.userid = userid; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public Date getAddtime() { return addtime; }
	public void setAddtime(Date addtime) { this.addtime = addtime; }
}
