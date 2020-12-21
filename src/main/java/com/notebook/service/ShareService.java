package com.notebook.service;

import com.notebook.domain.ShareDo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
public interface ShareService extends IService<ShareDo> {
    boolean increaseShareCollect(Integer shareId);

    boolean decreaseShareCollect(Integer shareId);

}
