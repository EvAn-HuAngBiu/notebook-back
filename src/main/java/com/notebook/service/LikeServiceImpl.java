package com.notebook.service;

import com.notebook.domain.LikeDo;
import com.notebook.dao.mapper.LikeMapper;
import com.notebook.service.LikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 点赞表 服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-11-14
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, LikeDo> implements LikeService {

}
