package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiyang.common.BaseController;
import com.haiyang.common.Result;
import com.haiyang.entity.Business;
import com.haiyang.entity.BusinessCategory;
import com.haiyang.service.BusinessCategoryService;
import com.haiyang.service.BusinessService;
import com.haiyang.service.BusinessCategoryService;
import com.haiyang.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    // 原有查询所有商家的方法
    @GetMapping("/list")
    public Result list() {
        List<Business> list = bService.list(new QueryWrapper<Business>
                ().ne("statu", 0));
        if (list == null) {
            return Result.fail(30001, "暂无商家数据显示", null);
        }
        return Result.success(list);
    }
    @GetMapping("/search")
    public Result searchBusiness(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 参数校验
        size = Math.min(size, 100);

        Page<Business> pageInfo = new Page<>(page, size);
        QueryWrapper<Business> qw = new QueryWrapper<>();

        qw.like("business_name", keyword)
                .or()
                .like("business_address", keyword)
                .or()
                .like("business_explain", keyword)
                .ne("statu", 0);  // 保持与list()方法一致的过滤条件

        IPage<Business> result = bService.page(pageInfo, qw);

        // 移除高亮处理或确保前端能安全渲染
        return Result.success(result);

    }
    @GetMapping("/listByCategoryId/{categoryId}")
    public Result listByCategoryId(@PathVariable Integer categoryId) {
        //步骤1：先通过分类编号 查询sys_business_category表中 某个分类下所有的商家编号。
        List<BusinessCategory> bcList = bcService.list(new QueryWrapper<BusinessCategory>().eq("category_id", categoryId));
        List<Business> businessList = new ArrayList<>();


        bcList.stream(). forEach(bc ->{
            Business business = bService.getById(bc.getBusinessId());
            businessList.add(business);
        });
        return Result.success(businessList);
    }
    @GetMapping("/info/{businessId}")
    public Result info(@PathVariable Long businessId){
        Business business = bService.getById(businessId);
        if(business == null){
            return  Result.fail("商家的详情数据加载失败");
        }else{
            return Result.success(business);
        }
    }

}