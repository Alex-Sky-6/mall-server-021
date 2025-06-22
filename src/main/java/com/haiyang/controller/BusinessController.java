package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Business;
import com.haiyang.entity.BusinessCategory;
import com.haiyang.service.BusinessCategoryService;
import com.haiyang.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.haiyang.common.BaseController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/business")
public class BusinessController extends BaseController {

    @Autowired
    private BusinessService bService;
    @Autowired
    private BusinessCategoryService bcService;

    @GetMapping("/info/{businessId}")
    public Result info(@PathVariable Long businessId) {
        Business business = bService.getById(businessId);
        if (business == null) {
            return Result.fail("商家的详情数据加载失败");
        } else {
            return Result.success(business);
        }
    }

    @GetMapping("/listByCategoryId/{categoryId}")
    public Result listByCategoryId(@PathVariable Integer categoryId) {
        //步骤1: 先通过分类编号 查询sys_business_category表中 某个分类下所有的商家编号。
        List<BusinessCategory> bcList = bcService.list(new QueryWrapper<BusinessCategory>().eq("category_id", categoryId));
        List<Business> businessList = new ArrayList<>();

        // // 原注释掉的 for 循环方式
        // for(int i=0;i<bcList.size();i++){
        //     Long businessId = bcList.get(i).getBusinessId();
        //     Business business = bService.getById(businessId);
        //     businessList.add(business);
        // }

        bcList.stream().forEach(bc -> {
            Business business = bService.getById(bc.getBusinessId());
            businessList.add(business);
        });

        return Result.success(businessList);
    }

    @GetMapping("/list")
    public Result list() {
        List<Business> list = bService.list(new QueryWrapper<Business>().ne("statu", 0));
        if (list == null) {
            return Result.fail(30001, "暂无商家数据显示", null);
        }
        return Result.success(list);
    }
}