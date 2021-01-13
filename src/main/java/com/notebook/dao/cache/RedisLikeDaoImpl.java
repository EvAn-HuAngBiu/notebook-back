package com.notebook.dao.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.dao.mapper.LikeMapper;
import com.notebook.dao.mapper.ShareMapper;
import com.notebook.domain.LikeDo;
import com.notebook.domain.ShareDo;
import com.notebook.service.LikeService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Project: notebook
 * File: RedisLikeDaoImpl
 *
 * @author evan
 * @date 2020/11/29
 */
@Slf4j
@Repository
@SuppressWarnings("all")
public class RedisLikeDaoImpl implements CachedLikeDao {
    public static final String SHARE_LIKE_NAMESPACE_PREFIX = "notebook:share:like:";

    /**
     * 用户赞了的分享 userid->SET shareId
     * */
    public static final String USER_LIKED_SHARE = SHARE_LIKE_NAMESPACE_PREFIX + "share_liked:";

    /**
     * 分享的总点赞数 hash shareid->count
     * */
    public static final String TOTAL_LIKE_COUNT = SHARE_LIKE_NAMESPACE_PREFIX + "total_like_count";

    /**
     * 赞我的总数 hash userid->count
     * */
    public static final String LIKE_ME_TOTAL_COUNT = SHARE_LIKE_NAMESPACE_PREFIX + "like_me_total_count";

    /**
     * 操作记录器
     * */
    public static final String OPERATION_RECORDER = SHARE_LIKE_NAMESPACE_PREFIX + "operation_record";

    /**
     * 新点赞计数器 hash userid->count
     * 取完立即重置为0
     * */
    public static final String NEW_LINKE_COUNTER = SHARE_LIKE_NAMESPACE_PREFIX + "new_like_counter";

    private final StringRedisTemplate template;
    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final ShareMapper shareMapper;

    public RedisLikeDaoImpl(StringRedisTemplate template, LikeMapper likeMapper,
                             ShareMapper shareMapper, LikeService likeService) {
        this.template = template;
        this.likeMapper = likeMapper;
        this.shareMapper = shareMapper;
        this.likeService = likeService;
    }

    @PostConstruct
    public void loadCache() {
        // 加载记录被赞的用户列表, (key->value)为(shareId->Set<userId>)
        SetOperations<String, String> setOperations = this.template.opsForSet();
        List<LikeDo> likes = this.likeMapper.selectList(new LambdaQueryWrapper<LikeDo>()
                .select(LikeDo::getShareId, LikeDo::getUserId));
        likes.stream().collect(Collectors.groupingBy(LikeDo::getUserId,
                Collectors.mapping(l -> l.getShareId().toString(), Collectors.toSet())))
                .forEach((k, v) -> setOperations.add(USER_LIKED_SHARE + k.toString(),
                        v.toArray(String[]::new)));

        // 加载记录的总点赞数
        HashOperations<String, Object, Object> hashOperations = this.template.opsForHash();
        Map<String, String> totalLikes = shareMapper.selectList(new LambdaQueryWrapper<ShareDo>()
                .select(ShareDo::getShareId, ShareDo::getLikeCnt))
                .stream()
                .collect(Collectors.toMap(s -> s.getShareId().toString(),
                        s -> s.getLikeCnt().toString()));
        hashOperations.putAll(TOTAL_LIKE_COUNT, totalLikes);

        // 加载用户被赞的总数
        Map<String, String> userBeLiked = this.likeMapper.selectUserLikedCount()
                .stream()
                .collect(Collectors.toMap(m -> ((Integer) m.get("user_id")).toString(),
                        m -> ((Long) m.get("cnt")).toString()));
        hashOperations.putAll(LIKE_ME_TOTAL_COUNT, userBeLiked);
    }

    @PreDestroy
    public void destroyCache() {
        saveData();
        this.template.delete(this.template.keys(USER_LIKED_SHARE + "*"));
        this.template.delete(TOTAL_LIKE_COUNT);
        this.template.delete(LIKE_ME_TOTAL_COUNT);
        this.template.delete(OPERATION_RECORDER);
        this.template.delete(NEW_LINKE_COUNTER);
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveData() {
        // 周期性将缓存写回数据库，抓住三个关键：点赞记录器、取消点赞记录器和记录总数变化量
        ListOperations<String, String> listOperation = template.opsForList();
        Long size = Optional.ofNullable(listOperation.size(OPERATION_RECORDER)).orElse(0L);
        List<String> operations = listOperation.range(OPERATION_RECORDER, 0, size);
        ObjectMapper mapper = new ObjectMapper();
        operations.forEach(op -> {
            try {
                Map<String, Integer> map = mapper.readValue(op, Map.class);
                if (map.get("type") == 0) {
                    this.likeService.save(new LikeDo(null, map.get("userId"), map.get("shareId"),
                            false, 0));
                } else {
                    this.likeService.remove(new LambdaQueryWrapper<LikeDo>()
                            .eq(LikeDo::getUserId, map.get("userId"))
                            .eq(LikeDo::getShareId, map.get("shareId")));
                }
            } catch (JsonProcessingException e) {
                log.error("Cannot read value: {}, exception is: {}", op, e.getMessage());
            }
        });
        this.template.delete(OPERATION_RECORDER);

        // 写总数记录器
        HashOperations<String, Object, Object> hashOperations = template.opsForHash();
        List<Object> shareKeys = List.copyOf(hashOperations.keys(TOTAL_LIKE_COUNT));
        if (!shareKeys.isEmpty()) {
            List<Object> shareValues = hashOperations.multiGet(TOTAL_LIKE_COUNT, shareKeys);
            for (int i = 0; i < shareKeys.size(); i++) {
                ShareDo share = new ShareDo();
                share.setLikeCnt(Integer.parseInt((String) shareValues.get(i)));
                this.shareMapper.update(share, new LambdaQueryWrapper<ShareDo>()
                        .eq(ShareDo::getShareId, Integer.parseInt((String) shareKeys.get(i))));
            }
        }
    }

    @Override
    public boolean checkIsLike(Integer userId, Integer shareId) {
        if (BooleanUtils.isTrue(this.template.hasKey(USER_LIKED_SHARE + userId.toString()))) {
            return BooleanUtils.isTrue(
                    this.template.opsForSet().isMember(USER_LIKED_SHARE + userId.toString(), shareId.toString()));
        }
        // 没有赞的记录，可以点赞
        return false;
    }

    @Override
    public List<Boolean> checkIsLikeBatch(Integer userId, List<Integer> shareIds) {
        return shareIds.stream().map(s -> checkIsLike(userId, s)).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void likeShare(Integer userId, Integer shareId) {
        if (!checkIsLike(userId, shareId)) {
            // 写入记录被赞的总数
            this.template.opsForHash().increment(TOTAL_LIKE_COUNT, shareId.toString(), 1L);
            // 写入赞我的总数
            ShareDo share = this.shareMapper.selectOne(new LambdaQueryWrapper<ShareDo>()
                    .eq(ShareDo::getShareId, shareId)
                    .select(ShareDo::getUserId)
                    .last("LIMIT 1"));
            this.template.opsForHash().increment(LIKE_ME_TOTAL_COUNT, share.getUserId().toString(), 1L);
            // 写入我赞的记录
            this.template.opsForSet().add(USER_LIKED_SHARE + userId.toString(), shareId.toString());
            // 写记录器
            Map<String, Integer> operation = new HashMap<>(3);
            operation.put("type", 0);
            operation.put("userId", userId);
            operation.put("shareId", shareId);
            ObjectMapper mapper = new ObjectMapper();
            String resultJson = mapper.writer().writeValueAsString(operation);
            this.template.opsForList().rightPush(OPERATION_RECORDER, resultJson);
            // 写入新点赞记录器
            this.template.opsForHash().increment(NEW_LINKE_COUNTER, share.getUserId().toString(), 1L);
        }
    }

    @SneakyThrows
    @Override
    public void dislikeShare(Integer userId, Integer shareId) {
        if (checkIsLike(userId, shareId)) {
            // 写入记录被赞的总数
            this.template.opsForHash().increment(TOTAL_LIKE_COUNT, shareId.toString(), -1L);
            // 写入赞我的总数
            ShareDo share = this.shareMapper.selectOne(new LambdaQueryWrapper<ShareDo>()
                    .eq(ShareDo::getShareId, shareId)
                    .select(ShareDo::getUserId)
                    .last("LIMIT 1"));
            this.template.opsForHash().increment(LIKE_ME_TOTAL_COUNT, share.getUserId().toString(), -1L);
            // 写入我赞的记录
            this.template.opsForSet().remove(USER_LIKED_SHARE + userId.toString(), shareId.toString());
            // 写记录器
            Map<String, Integer> operation = new HashMap<>(3);
            operation.put("type", 1);
            operation.put("userId", userId);
            operation.put("shareId", shareId);
            ObjectMapper mapper = new ObjectMapper();
            String resultJson = mapper.writer().writeValueAsString(operation);
            this.template.opsForList().rightPush(OPERATION_RECORDER, resultJson);
        }
    }

    @Override
    public int getTotalLike(Integer userId) {
        return Optional.ofNullable((String) this.template.opsForHash().get(LIKE_ME_TOTAL_COUNT, userId.toString()))
                .map(Integer::parseInt).orElse(0);
    }

    @Override
    public int getRecordLikes(Integer shareId) {
        return Optional.ofNullable((String) this.template.opsForHash().get(TOTAL_LIKE_COUNT, shareId.toString()))
                .map(Integer::parseInt).orElse(0);
    }

    @Override
    public int getNewCount(Integer userId) {
        Integer result = Optional.ofNullable((String) this.template.opsForHash().get(NEW_LINKE_COUNTER, userId.toString()))
                .map(Integer::parseInt).orElse(0);
        this.template.opsForHash().put(NEW_LINKE_COUNTER, userId.toString(), "0");
        return result;
    }
}
