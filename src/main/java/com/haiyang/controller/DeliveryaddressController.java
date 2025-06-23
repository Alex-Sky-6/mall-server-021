package com.haiyang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Deliveryaddress;
import com.haiyang.service.DeliveryaddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.haiyang.common.BaseController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/deliveryaddress")
@CrossOrigin
public class DeliveryaddressController extends BaseController {
    @Autowired
    private DeliveryaddressService dyService;
    @GetMapping("/list/{accountId}")
    public Result list(@PathVariable String accountId){
        QueryWrapper<Deliveryaddress> qw = new QueryWrapper<>();
        qw.eq("account_id",accountId);
        List<Deliveryaddress> list = dyService.list(qw);
        if(list == null){
            return Result.fail("配送信息加载失败");
        }else{
            return Result.success(list);
        }
    }
    @GetMapping("/detail/{daId}")
    public Result getDetail(@PathVariable Integer daId) {
        try {
            // 1. 参数校验
            if (daId == null || daId <= 0) {
                return Result.fail("地址ID不合法");
            }

            // 2. 查询数据库（添加状态条件）
            Deliveryaddress address = dyService.lambdaQuery()
                    .eq(Deliveryaddress::getDaId, daId)
                    .eq(Deliveryaddress::getStatu, 1) // 只查询有效地址
                    .one();

            // 3. 严格判空
            if (address == null) {
                return Result.fail("地址不存在或已被删除");
            }

            // 4. 返回格式化数据（匹配图片字段）
            return Result.success(address);
        } catch (Exception e) {
            return Result.fail("系统繁忙，请稍后重试");
        }
    }
    @PostMapping("/add")
    public Result add(@RequestBody Deliveryaddress deliveryaddress) {
        // 参数校验（根据实体类类型调整）
// 自动填充字段
        deliveryaddress.setCreated(LocalDateTime.now());  // 当前时间作为创建时间
        deliveryaddress.setUpdated(LocalDateTime.now());
        deliveryaddress.setStatu(1);            // 默认状态为1（有效）
        if (deliveryaddress.getContactName() == null || deliveryaddress.getContactName().trim().isEmpty()) {
            return Result.fail("收货人姓名不能为空");
        }

        if (deliveryaddress.getContactTel() == null || !deliveryaddress.getContactTel().matches("^1[3-9]\\d{9}$")) {
            return Result.fail("请输入正确的11位手机号");
        }

        if (deliveryaddress.getAddress() == null || deliveryaddress.getAddress().trim().isEmpty()) {
            return Result.fail("详细地址不能为空");
        }

        if (deliveryaddress.getAccountId() == null) {
            return Result.fail("用户ID不能为空");
        }

        // 设置默认性别（Integer类型，1=男，0=女）
        if (deliveryaddress.getContactSex() == null) {
            deliveryaddress.setContactSex(1); // 默认男性（注意是Integer类型）
        } else if (deliveryaddress.getContactSex() != 0 && deliveryaddress.getContactSex() != 1) {
            return Result.fail("性别参数不合法（0-女，1-男）");
        }

        // 保存到数据库
        boolean saved = dyService.save(deliveryaddress);
        return saved ? Result.success("地址添加成功") : Result.fail("地址添加失败");
    }
    // DeliveryaddressController.java
    @PutMapping("/update")
    public Result updateAddress(@RequestBody Deliveryaddress address) {
        // 参数校验（与图片字段对应）
        if (address.getDaId() == null) {
            return Result.fail("地址ID不能为空");
        }
        if (StringUtils.isEmpty(address.getContactName())) {
            return Result.fail("请输入收货人姓名");
        }
        if (!address.getContactTel().matches("^1[3-9]\\d{9}$")) {
            return Result.fail("手机号格式不正确");
        }
        if (StringUtils.isEmpty(address.getAddress())) {
            return Result.fail("请输入详细地址");
        }

        address.setUpdated(LocalDateTime.now());

        boolean success = dyService.lambdaUpdate()
                .eq(Deliveryaddress::getDaId, address.getDaId())
                .update(address);

        return success ? Result.success("地址修改成功") : Result.fail("地址修改失败");
    }
    // DeliveryaddressController.java
    @DeleteMapping("/delete/{daId}")
    public Result deleteAddress(@PathVariable Integer daId) {
        // 逻辑删除（设置状态为0，保留数据）
        boolean success = dyService.lambdaUpdate()
                .set(Deliveryaddress::getStatu, 0)
                .set(Deliveryaddress::getUpdated, LocalDateTime.now())
                .eq(Deliveryaddress::getDaId, daId)
                .update();

        return success ? Result.success("地址删除成功") : Result.fail("地址删除失败");
    }
}

