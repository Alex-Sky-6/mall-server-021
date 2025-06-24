package com.haiyang.service.impl;

import com.haiyang.entity.Comment;
import com.haiyang.mapper.CommentMapper;
import com.haiyang.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Alex
 * @since 2025-06-24
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
