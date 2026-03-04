package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 房屋证明资料任务
 */
@TableName("fangwuzhengming")
public class FangwuzhengmingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 关联房屋ID */
    private Long fangwuId;
    /** 提交人ID（户主） */
    private Long huzhuId;
    /** 证明资料文件名，逗号分隔 */
    private String zhengmingFiles;
    /** 任务状态：待提交/待审核/已通过/已驳回 */
    private String status;
    /** 审核备注/驳回原因 */
    private String shenheBeizhu;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date createTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFangwuId() { return fangwuId; }
    public void setFangwuId(Long fangwuId) { this.fangwuId = fangwuId; }
    public Long getHuzhuId() { return huzhuId; }
    public void setHuzhuId(Long huzhuId) { this.huzhuId = huzhuId; }
    public String getZhengmingFiles() { return zhengmingFiles; }
    public void setZhengmingFiles(String zhengmingFiles) { this.zhengmingFiles = zhengmingFiles; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getShenheBeizhu() { return shenheBeizhu; }
    public void setShenheBeizhu(String shenheBeizhu) { this.shenheBeizhu = shenheBeizhu; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
