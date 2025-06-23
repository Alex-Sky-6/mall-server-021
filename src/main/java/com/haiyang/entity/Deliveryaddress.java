package com.haiyang.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.haiyang.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_deliveryaddress")
public class Deliveryaddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 送货地址编号
     */
    @TableId(value = "da_id", type = IdType.AUTO)
    private Integer daId;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人性别（1-男，2-女）
     */
    @TableField("contact_sex")
    private Integer contactSex;

    /**
     * 联系人电话
     */
    @TableField("contact_tel")
    private String contactTel;

    /**
     * 送货地址
     */
    @TableField("address")
    private String address;

    /**
     * 关联用户ID
     */
    @TableField("account_id")
    private String accountId;


    /**
     * 创建时间（自动填充）
     */
    @TableField(value = "created", fill = FieldFill.INSERT)
    private LocalDateTime created;

    /**
     * 更新时间（自动填充）
     */
    @TableField(value = "updated", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updated;

    /**
     * 状态（1-有效，0-无效）
     */
    @TableField(value = "statu", fill = FieldFill.INSERT)
    private Integer statu;
}