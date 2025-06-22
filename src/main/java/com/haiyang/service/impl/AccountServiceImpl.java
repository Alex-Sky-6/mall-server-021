package com.haiyang.service.impl;

import com.haiyang.entity.Account;
import com.haiyang.mapper.AccountMapper;
import com.haiyang.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2025-06-19
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
