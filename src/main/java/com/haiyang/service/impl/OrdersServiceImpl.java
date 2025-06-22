package com.haiyang.service.impl;

import com.haiyang.entity.Orders;
import com.haiyang.mapper.OrdersMapper;
import com.haiyang.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2025-06-22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

}
