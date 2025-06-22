package com.haiyang.service.impl;

import com.haiyang.entity.Cart;
import com.haiyang.mapper.CartMapper;
import com.haiyang.service.CartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2025-06-21
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

}
