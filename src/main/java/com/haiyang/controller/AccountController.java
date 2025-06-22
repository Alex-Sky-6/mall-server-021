package com.haiyang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Const;
import com.haiyang.common.Result;
import com.haiyang.entity.Account;
import com.haiyang.service.AccountService;
import com.haiyang.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.haiyang.common.BaseController;

import java.time.LocalDateTime;

@CrossOrigin
@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController extends BaseController {
    @Autowired
    private AccountService aService;

    //手机号验证方法
    @GetMapping("/check/{accountId}")
    public Result check(@PathVariable String accountId) {
        Account account =aService.getById(accountId);
        if (account == null) {
            return Result.success("手机号可以注册");
        }else{
            return Result.fail(20005,"账户手机号已经注册",null);
        }
    }

    //注册方法
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        // 对页面提交密码在此加密
        account.setPassword(MD5Utils.md5(account.getPassword()));
        account.setCreated(LocalDateTime.now());
        account.setUpdated(LocalDateTime.now());
        account.setStatu(1); // 0禁用 1正常
        if (account.getAccountSex() == 1) {
            account.setAccountImg(Const.DEFAULT_IMG_1);
        } else {
            account.setAccountImg(Const.DEFAULT_IMG_0);
        }
        // 插入数据
        aService.save(account);
        return Result.success("用户信息注册成功");
    }


    //登录方法
    @PostMapping("/login")
    public Result login(String accountId, String password) {
        log.info("手机号为 {} 用户正在登录APP端--", accountId);

        //手机号在sys_account表中是主键，是不重复的，查询返回就是单一对象
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if (account == null) {
            return Result.fail("账户手机号不存在");
        }else{
            String newPwd = MD5Utils.md5(password);
            if (newPwd.equals(account.getPassword())) {
                if (account.getStatu() == 0) {
                    return Result.fail("该账户已被禁用或注销，暂不可用");
                }else{
                    return Result.success(account);
                }
            }else{
                return Result.fail("密码错误");
            }
        }
    }
}