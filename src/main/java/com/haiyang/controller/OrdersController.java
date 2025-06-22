package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Cart;
import com.haiyang.entity.Orders;
import com.haiyang.entity.Ordersdetailet;
import com.haiyang.service.CartService;
import com.haiyang.service.OrdersService;
import com.haiyang.service.OrdersdetailetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.haiyang.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController extends BaseController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersdetailetService odService;

    // 事务
    @Transactional
    @PostMapping("/save")
    public Result save(@RequestBody Orders orders) {
        // 步骤1：查询当前用户 购物车中商品数据
        QueryWrapper<Cart> qw = new QueryWrapper<Cart>();
        qw.eq("account_id", orders.getAccountId());
        qw.eq("business_id", orders.getBusinessId());
        List<Cart> cartList = cartService.list(qw);

        // 步骤2：保存订单
        orders.setCreated(LocalDateTime.now());
        orders.setUpdated(LocalDateTime.now());
        orders.setState(0);  // 0未支付  1已经支付
        ordersService.save(orders);  // 订单编号 order_id主键 自动增长
        // 获得录入订单 自增编号
        Long orderId = orders.getOrderId();

        // 步骤3：保存订单明细
        List<Ordersdetailet> odList = new ArrayList<>();
        cartList.forEach(cart -> {
            // 创建订单明细实体类
            Ordersdetailet od = new Ordersdetailet();
            od.setOrderId(orderId);
            od.setGoodsId(cart.getGoodsId());
            od.setQuantity(cart.getQuantity());

            odList.add(od);
        });
        // 将所有存储的订单明细对象 存入List集合，myBatisPlus提供批量保存。
        odService.saveBatch(odList);

        // 步骤4：删除购物车当前商家商品
        cartService.remove(qw);

        // 将订单保存生成的主键值 返回前端
        return Result.success(orderId);
    }
}