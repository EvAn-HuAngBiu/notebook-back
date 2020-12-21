package com.notebook.service;

import com.notebook.config.storage.FileStorageService;
import com.notebook.dao.cache.CachedCollectDao;
import com.notebook.dao.cache.CachedLikeDao;
import com.notebook.dao.mapper.ShareVoMapper;
import com.notebook.domain.RecordDo;
import com.notebook.domain.vo.ShareBriefVo;
import com.notebook.domain.vo.ShareUserInfoVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: notebook
 * File: ShareVoService
 *
 * @author evan
 * @date 2020/11/11
 */
@Service
public class ShareVoService {
    private final ShareVoMapper shareVoMapper;
    private final CachedLikeDao cachedLikeDao;
    private final CachedCollectDao cachedCollectDao;
    private final FileStorageService fileStorageService;

    public ShareVoService(ShareVoMapper shareVoMapper, CachedLikeDao cachedLikeDao,
                          FileStorageService fileStorageService, CachedCollectDao cachedCollectDao) {
        this.shareVoMapper = shareVoMapper;
        this.cachedLikeDao = cachedLikeDao;
        this.fileStorageService = fileStorageService;
        this.cachedCollectDao = cachedCollectDao;
    }

    private List<ShareBriefVo> handleShareBriefVos(List<ShareUserInfoVo> shareUserInfoVos) {
        shareUserInfoVos.forEach(s -> {
            s.getShareDo().setLikeCnt(cachedLikeDao.getRecordLikes(s.getShareDo().getShareId()));
            s.getShareDo().setCollectCnt(cachedCollectDao.getRecordCollects(s.getShareDo().getShareId()));
        });
        List<ShareBriefVo> result = new ArrayList<>(shareUserInfoVos.size());
        shareUserInfoVos.forEach(su -> {
            List<RecordDo> record = shareVoMapper.selectBriefRecordByShareId(su.getShareDo().getShareId());
            record.forEach(s -> s.setPicUrl(fileStorageService.generateUrls(s.getPicUrl())));
            result.add(new ShareBriefVo(su.getShareDo(), su.getUserDo(), record, false, false));
        });
        return result;
    }

    public List<ShareBriefVo> getShareBriefVoOrderByNew(Integer tagId, Integer page, Integer size) {
        List<ShareUserInfoVo> shareUserInfoVos = shareVoMapper.selectShareAndUserInfoOrderByNew(tagId, (page - 1) * size, size);
        return handleShareBriefVos(shareUserInfoVos);
    }

    public List<ShareBriefVo> getShareBriefVoOrderByHot(Integer tagId, Integer page, Integer size) {
        List<ShareUserInfoVo> shareUserInfoVos = shareVoMapper.selectShareAndUserInfoOrderByHot(tagId, (page - 1) * size, size);
        return handleShareBriefVos(shareUserInfoVos);
    }

    public List<ShareBriefVo> getShareBriefVoByUser(Integer userId, Integer page, Integer size) {
        List<ShareUserInfoVo> shareUserInfoVos = shareVoMapper.selectShareAndUserInfoByUser(userId, (page - 1) * size, size);
        return handleShareBriefVos(shareUserInfoVos);
    }

    public List<ShareBriefVo> getShareBriefByBatchIds(List<Integer> shareIds) {
        List<ShareUserInfoVo> shareUserInfoVos = shareIds.stream()
                .map(shareVoMapper::selectShareAndUserInfoById).collect(Collectors.toList());
        return handleShareBriefVos(shareUserInfoVos);
    }

    public List<RecordDo> getDetailRecord(Integer shareId) {
        return this.shareVoMapper.selectDetailRecordByShareId(shareId);
    }

    public ShareBriefVo getSpecifyShare(Integer shareId) {
        ShareUserInfoVo shareUserInfoVo = this.shareVoMapper.selectShareAndUserInfoById(shareId);
        shareUserInfoVo.getShareDo().setLikeCnt(cachedLikeDao.getRecordLikes(
                shareUserInfoVo.getShareDo().getShareId()));
        List<RecordDo> recordDo = shareVoMapper.selectBriefRecordByShareId(shareUserInfoVo.getShareDo().getShareId());
        return new ShareBriefVo(shareUserInfoVo.getShareDo(), shareUserInfoVo.getUserDo(), recordDo, false, false);
    }

    public void handleShareLikeAndCollect(List<ShareBriefVo> shareBriefVos, Integer userId) {
        shareBriefVos.forEach(s -> {
            s.setLike(cachedLikeDao.checkIsLike(userId, s.getShareDo().getShareId()));
            s.setCollect(cachedCollectDao.checkIsCollect(userId, s.getShareDo().getShareId()));
        });
    }
}
