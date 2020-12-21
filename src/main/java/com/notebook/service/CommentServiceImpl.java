package com.notebook.service;

import com.notebook.domain.CommentDo;
import com.notebook.dao.mapper.CommentMapper;
import com.notebook.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-12-21
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentDo> implements CommentService {

}
