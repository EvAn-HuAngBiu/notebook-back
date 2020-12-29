package com.notebook.service;

import com.notebook.dao.mapper.NotifyVoMapper;
import com.notebook.dao.mapper.ShareVoMapper;
import com.notebook.domain.NotifyDo;
import com.notebook.dao.mapper.NotifyMapper;
import com.notebook.domain.RecordDo;
import com.notebook.domain.dto.NotifyBriefDto;
import com.notebook.service.NotifyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-12-25
 */
@Service
public class NotifyServiceImpl extends ServiceImpl<NotifyMapper, NotifyDo> implements NotifyService {
    private final NotifyVoMapper notifyVoMapper;
    private final ShareVoMapper shareVoMapper;

    public NotifyServiceImpl(NotifyVoMapper notifyVoMapper, ShareVoMapper shareVoMapper) {
        this.notifyVoMapper = notifyVoMapper;
        this.shareVoMapper = shareVoMapper;
    }

    @Override
    public List<NotifyBriefDto> selectNotifyByUserId(Integer userId, Integer page, Integer size) {
        List<NotifyBriefDto> notifyBriefs = notifyVoMapper.selectNotifyByUserId(userId, (page - 1) * size, size);
        notifyBriefs.forEach(n -> {
            RecordDo record = shareVoMapper.selectOneRecordFromShare(n.getShareId());
            n.setRecordTitle(record.getRecordTitle());
            n.setRecordType(record.getRecordType());
        });
        return notifyBriefs;
    }
} 
