package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.entity.Comment;
import com.haiyang.entity.Orders;
import com.haiyang.entity.Ordersdetailet;
import com.haiyang.entity.Goods;
import com.haiyang.entity.Business;
import com.haiyang.service.CommentService;
import com.haiyang.service.OrdersService;
import com.haiyang.service.OrdersdetailetService;
import com.haiyang.service.GoodsService;
import com.haiyang.service.BusinessService;
import com.haiyang.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.haiyang.common.BaseController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  评论前端控制器
 * </p>
 *
 * @author Alex
 * @since 2025-06-24
 */
@RestController
@RequestMapping("/comment")
@CrossOrigin
@Slf4j
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private OrdersdetailetService ordersdetailetService;
    
    @Autowired
    private GoodsService goodsService;
    
    @Autowired
    private BusinessService businessService;

    /**
     * 根据用户ID查询用户的所有评论（个人中心-我的评论）
     * @param accountId 用户ID
     * @return 用户评论列表（包含商家名称和商品信息）
     */
    @GetMapping("/user/{accountId}")
    public Result getUserComments(@PathVariable String accountId) {
        try {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", accountId)
                       .orderByDesc("created"); // 按创建时间倒序排列
            
            List<Comment> commentList = commentService.list(queryWrapper);
            
            if (commentList == null || commentList.isEmpty()) {
                return Result.success("暂无评论数据");
            }
            
            // 为每个评论设置商家名称和商品信息
            for (Comment comment : commentList) {
                // 设置商家名称
                if (comment.getBusinessId() != null) {
                    Business business = businessService.getById(comment.getBusinessId());
                    if (business != null) {
                        comment.setBusinessName(business.getBusinessName());
                    }
                }
                
                // 设置商品信息（获取订单中的第一个商品作为代表）
                if (comment.getOrderId() != null) {
                    QueryWrapper<Ordersdetailet> odWrapper = new QueryWrapper<>();
                    odWrapper.eq("order_id", comment.getOrderId());
                    List<Ordersdetailet> orderDetails = ordersdetailetService.list(odWrapper);
                    
                    if (!orderDetails.isEmpty()) {
                        // 获取第一个商品信息
                        Goods goods = goodsService.getById(orderDetails.get(0).getGoodsId());
                        if (goods != null) {
                            comment.setGoods(goods);
                        }
                    }
                }
            }
            
            return Result.success(commentList);
        } catch (Exception e) {
            return Result.fail("查询用户评论失败：" + e.getMessage());
        }
    }

    /**
     * 根据商家ID查询商家的所有评论（商家详情-商家评论）
     * @param businessId 商家ID
     * @return 商家评论列表（包含商品信息）
     */
    @GetMapping("/business/{businessId}")
    public Result getBusinessComments(@PathVariable Long businessId) {
        try {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_id", businessId)
                       .orderByDesc("created"); // 按创建时间倒序排列
            
            List<Comment> commentList = commentService.list(queryWrapper);
            
            if (commentList == null || commentList.isEmpty()) {
                return Result.success("暂无评论数据");
            }
            
            // 为每个评论设置商品信息
            for (Comment comment : commentList) {
                // 设置商品信息（获取订单中的第一个商品作为代表）
                if (comment.getOrderId() != null) {
                    QueryWrapper<Ordersdetailet> odWrapper = new QueryWrapper<>();
                    odWrapper.eq("order_id", comment.getOrderId());
                    List<Ordersdetailet> orderDetails = ordersdetailetService.list(odWrapper);
                    
                    if (!orderDetails.isEmpty()) {
                        // 获取第一个商品信息
                        Goods goods = goodsService.getById(orderDetails.get(0).getGoodsId());
                        if (goods != null) {
                            comment.setGoods(goods);
                        }
                    }
                }
            }
            
            return Result.success(commentList);
        } catch (Exception e) {
            return Result.fail("查询商家评论失败：" + e.getMessage());
        }
    }

    /**
     * 根据商家ID和评分筛选查询评论（商家详情-评论筛选）
     * @param businessId 商家ID
     * @param ratingFilter 评分筛选条件：all(全部), good(好评>=4), medium(中评=3), bad(差评<=2)
     * @return 筛选后的评论列表（包含商品信息）
     */
    @GetMapping("/business/{businessId}/filter")
    public Result getBusinessCommentsByRating(@PathVariable Long businessId, 
                                            @RequestParam(defaultValue = "all") String ratingFilter) {
        try {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_id", businessId);
            
            // 根据评分筛选条件添加查询条件
        switch (ratingFilter) {
            case "good":
                queryWrapper.ge("rate", 4.0); // 好评：评分 >= 4
                break;
            case "medium":
                queryWrapper.between("rate", 2.1, 3.9); // 中评：评分在 2 到 4 之间（不包含4）
                break;
            case "bad":
                queryWrapper.le("rate", 2.0); // 差评：评分 <= 2
                break;
            case "all":
            default:
                // 不添加评分筛选条件，查询全部
                break;
        }

            
            queryWrapper.orderByDesc("created"); // 按创建时间倒序排列
            
            List<Comment> commentList = commentService.list(queryWrapper);
            
            if (commentList == null || commentList.isEmpty()) {
                return Result.success("暂无评论数据");
            }
            
            // 为每个评论设置商品信息
            for (Comment comment : commentList) {
                // 设置商品信息（获取订单中的第一个商品作为代表）
                if (comment.getOrderId() != null) {
                    QueryWrapper<Ordersdetailet> odWrapper = new QueryWrapper<>();
                    odWrapper.eq("order_id", comment.getOrderId());
                    List<Ordersdetailet> orderDetails = ordersdetailetService.list(odWrapper);
                    
                    if (!orderDetails.isEmpty()) {
                        // 获取第一个商品信息
                        Goods goods = goodsService.getById(orderDetails.get(0).getGoodsId());
                        if (goods != null) {
                            comment.setGoods(goods);
                        }
                    }
                }
            }
            
            return Result.success(commentList);
        } catch (Exception e) {
            return Result.fail("查询商家评论失败：" + e.getMessage());
        }
    }

    /**
     * 删除评论（个人中心-我的评论-删除功能）
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{commentId}")
    public Result deleteComment(@PathVariable Integer commentId) {
        try {
            boolean success = commentService.removeById(commentId);
            
            if (success) {
                return Result.success("评论删除成功");
            } else {
                return Result.fail("评论删除失败");
            }
        } catch (Exception e) {
            return Result.fail("删除评论失败：" + e.getMessage());
        }
    }
    @PostMapping("/save")
    public Result save(@RequestBody Comment comment) {
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());
        comment.setStatu(1);
        boolean success = commentService.save(comment);
        if (success) {
            // 2. 正确的 Result 用法（假设 Result 有 success(String msg) 方法）
            return Result.success("评论保存成功");
        } else {
            return Result.fail("评论保存失败");
        }
    }

}
