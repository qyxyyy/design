package com.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 用户表（统一：租客/房东/管理员）
 * identity_type: 1-租客 2-房东 3-管理员
 */
@TableName("user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	private String username;
	private String password;
	private String realName;
	private String phone;
	private String email;
	private String avatar;
	/** 身份类型：1-租客 2-房东 3-管理员 */
	private Integer identityType;
	private Integer gender;
	private String address;
	private Integer status;
	private Date createTime;
	private Date updateTime;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getRealName() { return realName; }
	public void setRealName(String realName) { this.realName = realName; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getAvatar() { return avatar; }
	public void setAvatar(String avatar) { this.avatar = avatar; }
	public Integer getIdentityType() { return identityType; }
	public void setIdentityType(Integer identityType) { this.identityType = identityType; }
	public Integer getGender() { return gender; }
	public void setGender(Integer gender) { this.gender = gender; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public Integer getStatus() { return status; }
	public void setStatus(Integer status) { this.status = status; }
	public Date getCreateTime() { return createTime; }
	public void setCreateTime(Date createTime) { this.createTime = createTime; }
	public Date getUpdateTime() { return updateTime; }
	public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

	/** 兼容旧逻辑：按 identity_type 返回角色名（管理员/户主/租客） */
	public String getRole() {
		if (identityType == null) return "";
		switch (identityType) {
			case 3: return "管理员";
			case 2: return "户主";
			case 1: return "租客";
			default: return "";
		}
	}
}
