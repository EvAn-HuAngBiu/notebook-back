package com.notebook.dao.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.dao.mapper.ShareMapper;
import com.notebook.domain.CollectDo;
import com.notebook.domain.ShareDo;
import com.notebook.service.CollectService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: notebook
 * File: RedisCollectDaoImpl
 *
 * @author evan
 * @date 2020/12/14
 */
@Slf4j
@Repository
@SuppressWarnings("all")
public class RedisCollectDaoImpl implements CachedCollectDao {
    public static final String SHARE_COLLECT_NAMESPACE_PREFIX = "notebook:share:collect:";

    /**
     * 用户收藏的分享 userid->SET shareId
     * */
    public static final String USER_COLLECTED_SHARE = SHARE_COLLECT_NAMESPACE_PREFIX + "share_collected:";

    /**
     * 分享的总收藏数 hash shareid->count
     * */
    public static final String TOTAL_COLLECT_COUNT = SHARE_COLLECT_NAMESPACE_PREFIX + "total_like_count";

    /**
     * 操作记录器
     * */
    public static final String OPERATION_RECORDER = SHARE_COLLECT_NAMESPACE_PREFIX + "operation_record";

    public final StringRedisTemplate template;
    public final CollectService collectService;
    public final ShareMapper shareMapper;

    public RedisCollectDaoImpl(StringRedisTemplate template, CollectService collectService,
                               ShareMapper shareMapper) {
        this.template = template;
        this.collectService = collectService;
        this.shareMapper = shareMapper;
    }

    @PostConstruct
    public void init() {
        // 加载用户收藏的分享
        SetOperations<String, String> setOperation = template.opsForSet();
        List<CollectDo> collectList = collectService.list(new LambdaQueryWrapper<CollectDo>()
                .select(CollectDo::getShareId, CollectDo::getUserId));
        collectList.forEach(c -> setOperation.add(USER_COLLECTED_SHARE + c.getUserId().toString(), c.getShareId().toString()));

        // 加载所有分享的收藏数
        HashOperations<String, Object, Object> hashOperation = template.opsForHash();
        List<ShareDo> shareList = shareMapper.selectList(new LambdaQueryWrapper<ShareDo>()
                .select(ShareDo::getShareId, ShareDo::getCollectCnt));
        shareList.forEach(s -> hashOperation.put(TOTAL_COLLECT_COUNT, s.getShareId().toString(), s.getCollectCnt().toString()));
    }

    @PreDestroy
    public void destroy() {
        saveData();
        this.template.delete(this.template.keys(USER_COLLECTED_SHARE + "*"));
        this.template.delete(TOTAL_COLLECT_COUNT);
        this.template.delete(OPERATION_RECORDER);
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveData() {
        // 写变化记录器
        ListOperations<String, String> listOperation = template.opsForList();
        Long size = Optional.ofNullable(listOperation.size(OPERATION_RECORDER)).orElse(0L);
        List<String> operations = listOperation.range(OPERATION_RECORDER, 0, size);
        ObjectMapper mapper = new ObjectMapper();
        operations.forEach(op -> {
            try {
                Map<String, Integer> map = mapper.readValue(op, Map.class);
                if (map.get("type") == 0) {
                    this.collectService.save(new CollectDo(null, map.get("userId"), map.get("shareId"), false, 0));
                } else {
                    this.collectService.remove(new LambdaQueryWrapper<CollectDo>()
                            .eq(CollectDo::getUserId, map.get("userId"))
                            .eq(CollectDo::getShareId, map.get("shareId")));
                }
            } catch (JsonProcessingException e) {
                log.error("Cannot read value: {}, exception is: {}", op, e.getMessage());
                throw new RuntimeException(e);
            }
        });
        this.template.delete(OPERATION_RECORDER);

        // 写总数记录器
        HashOperations<String, Object, Object> hashOperations = template.opsForHash();
        List<Object> shareKeys = List.copyOf(hashOperations.keys(TOTAL_COLLECT_COUNT));
        if (!shareKeys.isEmpty()) {
            List<Object> shareValues = hashOperations.multiGet(TOTAL_COLLECT_COUNT, shareKeys);
            for (int i = 0; i < shareKeys.size(); i++) {
                ShareDo share = new ShareDo();
                share.setCollectCnt(Integer.parseInt((String) shareValues.get(i)));
                this.shareMapper.update(share, new LambdaQueryWrapper<ShareDo>()
                        .eq(ShareDo::getShareId, Integer.parseInt((String) shareKeys.get(i))));
            }
        }
    }

    @Override
    public boolean checkIsCollect(Integer userId, Integer shareId) {
        if (BooleanUtils.isTrue(this.template.hasKey(USER_COLLECTED_SHARE + userId.toString()))) {
            return BooleanUtils.isTrue(
                    this.template.opsForSet().isMember(USER_COLLECTED_SHARE + userId.toString(), shareId.toString()));
        }
        // 没有赞的记录，可以点赞
        return false;
    }

    @Override
    public List<Boolean> checkIsCollectBatch(Integer userId, List<Integer> shareIds) {
        return shareIds.stream().map(s -> checkIsCollect(userId, s)).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void collectShare(Integer userId, Integer shareId) {
        // 写入收藏总数
        this.template.opsForHash().increment(TOTAL_COLLECT_COUNT, shareId.toString(), 1L);
        // 写入我的收藏
        this.template.opsForSet().add(USER_COLLECTED_SHARE + userId.toString(), shareId.toString());
        // 写记录器
        Map<String, Integer> operation = new HashMap<>(3);
        operation.put("type", 0);
        operation.put("userId", userId);
        operation.put("shareId", shareId);
        ObjectMapper mapper = new ObjectMapper();
        String resultJson = mapper.writer().writeValueAsString(operation);
        this.template.opsForList().rightPush(OPERATION_RECORDER, resultJson);
    }

    @SneakyThrows
    @Override
    public void cancelCollectShare(Integer userId, Integer shareId) {
        // 写入收藏总数
        this.template.opsForHash().increment(TOTAL_COLLECT_COUNT, shareId.toString(), -1L);
        // 写入我的收藏
        this.template.opsForSet().remove(USER_COLLECTED_SHARE + userId.toString(), shareId.toString());
        // 写记录器
        Map<String, Integer> operation = new HashMap<>(3);
        operation.put("type", 1);
        operation.put("userId", userId);
        operation.put("shareId", shareId);
        ObjectMapper mapper = new ObjectMapper();
        String resultJson = mapper.writer().writeValueAsString(operation);
        this.template.opsForList().rightPush(OPERATION_RECORDER, resultJson);
    }

    @Override
    public Set<Integer> getUserCollects(Integer userId) {
        return this.template.opsForSet().members(USER_COLLECTED_SHARE + userId.toString())
                .stream().map(Integer::parseInt).collect(Collectors.toSet());
    }

    @Override
    public int getRecordCollects(Integer shareId) {
        return Optional.ofNullable((String) this.template.opsForHash().get(TOTAL_COLLECT_COUNT, shareId.toString()))
                .map(Integer::parseInt).orElse(0);
    }

    @Override
    public long getUserTotalCollects(Integer userId) {
        return Optional.ofNullable(this.template.opsForSet().size(USER_COLLECTED_SHARE + userId.toString()))
                .orElse(0L);
    }

    @Override
    public List<Integer> getPagedUserCollects(Integer userId, Integer page, Integer size) {
        List<Integer> allCollectShareIds = List.copyOf(getUserCollects(userId));
        int begIdx = (page - 1) * size, endIdx = begIdx + size;
        if (begIdx >= allCollectShareIds.size()) {
            return Collections.EMPTY_LIST;
        }
        return allCollectShareIds.subList(begIdx,
                endIdx > allCollectShareIds.size() ? allCollectShareIds.size() : endIdx);
    }
}
