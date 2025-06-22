package com.haiyang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Goods;
import com.haiyang.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.haiyang.common.BaseController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseController {
    @Autowired
    private GoodsService gService;

    @GetMapping("/listByBusinessId/{businessId}")
    public Result listByBusinessId(@PathVariable Long businessId) {
        List<Goods> goodsList = gService.list(new QueryWrapper<Goods>().eq("business_id", businessId));
        if (goodsList == null) {
            return Result.fail("商品数据加载失败");
        } else {
            return Result.success(goodsList);
        }
    }

}
