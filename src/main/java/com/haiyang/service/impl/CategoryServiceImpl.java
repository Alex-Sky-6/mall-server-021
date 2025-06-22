package com.haiyang.service.impl;

import com.haiyang.entity.Category;
import com.haiyang.mapper.CategoryMapper;
import com.haiyang.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2025-06-17
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
