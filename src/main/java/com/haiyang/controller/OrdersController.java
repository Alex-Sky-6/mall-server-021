package com.haiyang.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Business;
import com.haiyang.entity.Cart;
import com.haiyang.entity.Goods;
import com.haiyang.entity.Orders;
import com.haiyang.entity.Ordersdetailet;
import com.haiyang.service.BusinessService;
import com.haiyang.service.CartService;
import com.haiyang.service.GoodsService;
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
    @Autowired
    private BusinessService bService;
    @Autowired
    private GoodsService goodsService;

    // 新增接口，通过订单号获取商家信息和订单详细内容
    @GetMapping("/businessInfo/{orderId}")
    public Result getBusinessInfoByOrderId(@PathVariable Long orderId) {
        try {
            // 1. 查询订单（带商家 ID）
            QueryWrapper<Orders> orderWrapper = new QueryWrapper<>();
            orderWrapper.eq("order_id", orderId); // 订单表的 order_id
            orderWrapper.orderByDesc("created");
            List<Orders> ordersList = ordersService.list(orderWrapper);

            if (ordersList.isEmpty()) {
                return Result.fail("订单不存在");
            }

            Orders order = ordersList.get(0);
            
            // 2. 从订单中提取商家 ID
            Long businessId = order.getBusinessId();
            if (businessId == null) {
                return Result.fail("订单未关联商家");
            }

            // 3. 查询商家信息
            QueryWrapper<Business> businessWrapper = new QueryWrapper<>();
            businessWrapper.eq("business_id", businessId); // 商家表的 business_id
            Business businessInfo = bService.getOne(businessWrapper);

            if (businessInfo == null) {
                return Result.fail("商家信息不存在");
            }

            // 4. 查询订单明细
            QueryWrapper<Ordersdetailet> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("order_id", orderId);
            List<Ordersdetailet> orderDetails = odService.list(detailWrapper);

            // 5. 查询商品信息（从订单明细中获取商品ID）
            Goods goodsInfo = null;
            if (!orderDetails.isEmpty()) {
                Integer goodsId = orderDetails.get(0).getGoodsId();
                if (goodsId != null) {
                    goodsInfo = goodsService.getById(goodsId);
                }
            }

            // 6. 将商家信息、订单明细和商品信息设置到订单对象中
            final Goods finalGoodsInfo = goodsInfo;
            ordersList.forEach(orderItem -> {
                orderItem.setBusiness(businessInfo);
                // 设置订单明细
                if (!orderDetails.isEmpty()) {
                    orderItem.setOrdersdetailet(orderDetails.get(0));
                }
                // 设置商品信息
                if (finalGoodsInfo != null) {
                    orderItem.setGoods(finalGoodsInfo);
                }
            });

            return Result.success(ordersList);
        } catch (Exception e) {
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    //事务
    @Transactional
    @PostMapping("/save")
    public Result save(@RequestBody Orders orders){
        //步骤1：查询当前用户 购物车中商品数据
        QueryWrapper<Cart> qw = new QueryWrapper<Cart>();
        qw.eq("account_id",orders.getAccountId());
        qw.eq("business_id",orders.getBusinessId());
        List<Cart> cartList = cartService.list();
        //步骤2：保存订单
        orders.setCreated(LocalDateTime.now());
        orders.setUpdated(LocalDateTime.now());
        orders.setStatu(0);   //0未支付   1已经支付
        ordersService.save(orders);  //订单编号 order_id主键 自动增长
        //获得录入订单 自增编号
        Long orderId = orders.getOrderId();
        //步骤3：保存订单明细
        List<Ordersdetailet> odList = new ArrayList<>();
        cartList.forEach(cart -> {
            //创建订单明细实体类
            Ordersdetailet od = new Ordersdetailet();
            od.setOrderId(orderId);
            od.setGoodsId(cart.getGoodsId());
            od.setQuantity(cart.getQuantity());
            odList.add(od);
        });
        //将所有存储的订单明细对象 存入List集合，myBatisPlus提供批量保存。
        odService.saveBatch(odList);
        //步骤4：删除购物车当前商家商品
        cartService.remove(qw);
        //将订单保存生成的主键值 返回前端
        return Result.success(orderId);
    }

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/listOrdersByAccountId/{accountId}")
    public Result listOrdersByAccountId(@PathVariable String accountId) {
        try {
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", accountId);
            queryWrapper.orderByDesc("created"); // 按创建时间倒序
            List<Orders> ordersList = ordersService.list(queryWrapper);
            return Result.success(ordersList);
        } catch (Exception e) {
            return Result.fail("查询订单失败：" + e.getMessage());
        }
    }

    /**
     * 更新订单支付状态
     * @param orderId 订单ID
     * @param status 支付状态 (0:未支付, 1:已支付)
     */
    @PutMapping("/updatePaymentStatus/{orderId}/{status}")
    public Result updatePaymentStatus(@PathVariable Long orderId, @PathVariable Integer status) {
        try {
            // 查询订单是否存在
            Orders order = ordersService.getById(orderId);
            if (order == null) {
                return Result.fail("订单不存在");
            }

            // 更新支付状态
            order.setStatu(status);
            order.setUpdated(LocalDateTime.now());

            boolean success = ordersService.updateById(order);
            if (success) {
                return Result.success("支付状态更新成功");
            } else {
                return Result.fail("支付状态更新失败");
            }
        } catch (Exception e) {
            return Result.fail("更新支付状态失败：" + e.getMessage());
        }
    }

}