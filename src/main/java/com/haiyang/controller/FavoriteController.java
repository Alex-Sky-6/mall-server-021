package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Business;
import com.haiyang.entity.Favorite;
import com.haiyang.service.BusinessService;
import com.haiyang.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  收藏功能前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-22
 */
@RestController
@CrossOrigin
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private BusinessService businessService;

    /**
     * 获取用户的所有收藏记录
     * @param accountId 用户ID
     * @return 用户的收藏列表
     */
//    @GetMapping("/byAccount/{accountId}")
//    public Result listByAccountId(@PathVariable String accountId) {
//        // 1. 查询收藏记录
//        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Favorite::getAccountId, accountId)
//                .eq(Favorite::getStatu, 1);
//        List<Favorite> favorites = favoriteService.list(queryWrapper);
//
//        if (favorites.isEmpty()) {
//            return Result.success(Collections.emptyList());
//        }
//
//        // 2. 提取商家ID
//        List<Long> businessIds = favorites.stream()
//                .map(Favorite::getBusinessId)
//                .collect(Collectors.toList());
//
//        // 3. 查询商家信息
//        List<Business> businesses = businessService.listByIds(businessIds);
//
//        return Result.success(businesses);
//    }
    @GetMapping("/byAccount/{accountId}")
    public Result listByAccountId(@PathVariable Long accountId) {
        // 1. 查询收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getAccountId, accountId)
                .eq(Favorite::getStatu, 1);
        List<Favorite> favorites = favoriteService.list(queryWrapper);

        if (favorites.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 2. 提取商家ID
        List<Long> businessIds = favorites.stream()
                .map(Favorite::getBusinessId)
                .collect(Collectors.toList());

        // 3. 查询商家信息
        List<Business> businesses = businessService.listByIds(businessIds);

        return Result.success(businesses);
    }


    /**
     * 添加收藏
     * @param favorite 收藏对象(包含accountId和businessId)
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result saveFavorite(@RequestBody Favorite favorite) {
        // 检查是否已存在收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getAccountId, favorite.getAccountId())
                .eq(Favorite::getBusinessId, favorite.getBusinessId());

        Favorite existing = favoriteService.getOne(queryWrapper);

        if(existing != null) {
            // 如果存在但状态为0(取消收藏)，则更新为1(有效)
            if(existing.getStatu() == 0) {
                existing.setStatu(1);
                existing.setUpdated(LocalDateTime.now());
                boolean updated = favoriteService.updateById(existing);
                return updated ?
                        Result.success("收藏成功") :
                        Result.fail("收藏操作失败");
            } else {
                return Result.success("已收藏该商家");
            }
        } else {
            // 设置状态为有效收藏
            favorite.setCreated(LocalDateTime.now());
            favorite.setStatu(1);
            boolean saved = favoriteService.save(favorite);
            return saved ?
                    Result.success("收藏成功") :
                    Result.fail("收藏操作失败");
        }
    }

    /**
     * 取消收藏
     * @param accountId 用户ID
     * @param businessId 商家ID
     * @return 操作结果
     */
    @PostMapping("/remove")
    public Result removeFavorite(@RequestParam String accountId, @RequestParam Long businessId) {
        // 查找收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getAccountId, accountId)
                .eq(Favorite::getBusinessId, businessId)
                .eq(Favorite::getStatu, 1); // 只查询有效的收藏

        Favorite favorite = favoriteService.getOne(queryWrapper);

        if (favorite != null) {
            // 将状态设置为0(取消收藏)
            favorite.setStatu(0);
            boolean updated = favoriteService.updateById(favorite);
            return updated ? Result.success("已取消收藏") : Result.fail("取消收藏失败");
        } else {
            return Result.fail("未找到收藏记录");
        }
    }
}