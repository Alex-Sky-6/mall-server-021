package com.haiyang.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author Alex
 * @since 2025-06-22
 */
@Data
@TableName("sys_ordersdetailet")
public class Ordersdetailet {

    private static final long serialVersionUID = 1L;

    /**
     * 订单明细编号
     */
    @TableId(value = "od_id", type = IdType.AUTO)
    private Long odId;

    /**
     * 所属订单编号sys_orders表order_id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 食品编号--sys_goods表goods_id
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;


}
