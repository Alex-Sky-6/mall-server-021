package com.haiyang.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiyang.common.Result;
import com.haiyang.entity.Account;
import com.haiyang.service.AccountService;
import com.haiyang.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.haiyang.common.BaseController;
import com.haiyang.common.Const;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2025-06-19
 */
@RestController
@RequestMapping("/account")
@Slf4j
@CrossOrigin
public class AccountController extends BaseController {

    @Autowired
    private AccountService aService;

    //手机号验证方法
    @GetMapping("/check/{accountId}")
    public Result check(@PathVariable String accountId){
        Account account = aService.getById(accountId);
        if(account == null){
            return Result.success("手机号可以注册");
        }else{
            return Result.fail(20005,"手机号已经被注册",null);
        }
    }

    //修改用户昵称
    @PostMapping("/updatename")
    public Result updatename(String nickname, String accountId){
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if(nickname.equals(account.getAccountName())){
            return Result.fail("用户名不能与之前一致");
        }else{
            account.setAccountName(nickname);
            account.setUpdated(LocalDateTime.now());
            aService.updateById(account);
            return Result.success("用户名修改成功");
        }
    }

    //修改用户性别
    @PostMapping("/updatesex")
    public Result updatesex(Integer accountSex, String accountId){
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if(accountSex.equals(account.getAccountSex())){
            return Result.fail("性别不能与之前一致");
        }else{
            account.setAccountSex(accountSex);
            account.setUpdated(LocalDateTime.now());
            if(account.getAccountSex() == 1){
                account.setAccountImg(Const.DEFAULT_IMG_1);
            }else{
                account.setAccountImg(Const.DEFAULT_IMG_0);
            }
            aService.updateById(account);
            return Result.success("性别修改成功");
        }
    }

    //注销账号
    @PostMapping("/logout/{accountId}")
    public Result logout(@PathVariable String accountId){
        Account account = aService.getById(accountId);
        account.setStatu(0);
        account.setUpdated(LocalDateTime.now());
        aService.updateById(account);
        return Result.success("注销成功");
    }



    @PostMapping("/register")
    public Result register(@RequestBody Account account){
        //对页面提交的密码在此加密
        account.setPassword(MD5Utils.md5(account.getPassword()));
        account.setCreated(LocalDateTime.now());
        account.setUpdated(LocalDateTime.now());
        account.setStatu(1);    //0禁用 1正常
        if(account.getAccountSex() == 1){
            account.setAccountImg(Const.DEFAULT_IMG_1);
        }else{
            account.setAccountImg(Const.DEFAULT_IMG_0);
        }
        //插入数据
        aService.save(account);
        return Result.success("用户信息注册成功");
    }

    @PostMapping("/login")
    public Result login(String accountId, String password) { //前端传来的数据
        log.info("手机号为 {} 用户正在登录APP端--", accountId);
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if (account == null) {
            return Result.fail("账户手机号不存在");
        } else {
            //account不等于null，说明账号手机号存在，需要比较密码是否一致
            String newPwd = MD5Utils.md5(password);   //将登录原文密码先加密变为密文
            if (newPwd.equals(account.getPassword()))
                if (account.getStatu() == 0) {
                    return Result.fail("该账户被禁用或被注销，暂无法使用");
                } else {
                    //登录成功，直接反馈登录账户信息
                    return Result.success(account);
                }
            else {
                return Result.fail("登录密码错误");
            }
        }
    }

    // 用户密码验证（启用该接口）
    @PostMapping("/checkpassword")
    public Result checkpassword(String accountId, String password){
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
//        if(account == null) {
//            return Result.fail(20010, "账户不存在", null);
//        }
        String newPwd = MD5Utils.md5(password);
        if(newPwd.equals(account.getPassword())){
            return Result.success("密码正确");
        }else{
            return Result.fail(20010, "密码错误", null);
        }
    }

    //修改密码
    @PostMapping("/updatepassword")
    public Result updatepassword(String oldPassword, String newPassword,String accountId){
        Account account = aService.getOne(new QueryWrapper<Account>().eq("account_id", accountId));
        if(oldPassword.equals(newPassword)){
            return Result.fail(20010, "更改的密码不能与原密码一致", null);
        }
        String newPwd = MD5Utils.md5(newPassword);
        account.setPassword(MD5Utils.md5(newPassword));
        account.setUpdated(LocalDateTime.now());
        aService.updateById(account);
        return Result.success("密码修改成功");
    }
}

