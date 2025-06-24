package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.entity.Comment;
import com.haiyang.service.CommentService;
import com.haiyang.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.haiyang.common.BaseController;

import java.util.List;

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
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    /**
     * 根据用户ID查询用户的所有评论（个人中心-我的评论）
     * @param accountId 用户ID
     * @return 用户评论列表
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
            
            return Result.success(commentList);
        } catch (Exception e) {
            return Result.fail("查询用户评论失败：" + e.getMessage());
        }
    }

    /**
     * 根据商家ID查询商家的所有评论（商家详情-商家评论）
     * @param businessId 商家ID
     * @return 商家评论列表
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
            
            return Result.success(commentList);
        } catch (Exception e) {
            return Result.fail("查询商家评论失败：" + e.getMessage());
        }
    }

    /**
     * 根据商家ID和评分筛选查询评论（商家详情-评论筛选）
     * @param businessId 商家ID
     * @param ratingFilter 评分筛选条件：all(全部), good(好评>=4), medium(中评=3), bad(差评<=2)
     * @return 筛选后的评论列表
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
                    queryWrapper.ge("rate", 4.0); // 好评：评分>=4
                    break;
                case "medium":
                    queryWrapper.eq("rate", 3.0); // 中评：评分=3
                    break;
                case "bad":
                    queryWrapper.le("rate", 2.0); // 差评：评分<=2
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

}
