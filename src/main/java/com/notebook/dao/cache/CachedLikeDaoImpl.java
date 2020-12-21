package com.notebook.dao.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notebook.dao.mapper.LikeMapper;
import com.notebook.dao.mapper.ShareMapper;
import com.notebook.domain.LikeDo;
import com.notebook.domain.ShareDo;
import com.notebook.service.LikeService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.HashOperations;
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
 * File: CachedLikeDaoImpl
 *
 * @author evan
 * @date 2020/11/13
 */
@Deprecated
public class CachedLikeDaoImpl implements CachedLikeDao {
    public static final String LIKE_FUNCTION_PREFIX = "share:like:";
    /**
     * 赞当前这条记录的用户, 结构 shareId->Set<userId>
     */
    public static final String SHARE_LIKED_USERS = LIKE_FUNCTION_PREFIX + "share_liked:";
    /**
     * 当前记录被赞的总数, 结构 shareId->cnt
     */
    public static final String SHARE_TOTAL_LIKES = LIKE_FUNCTION_PREFIX + "total_like";
    /**
     * 用户被赞的总数, 结构 userId->cnt
     */
    public static final String SHARE_USER_LIKES = LIKE_FUNCTION_PREFIX + "like_share";
    /**
     * 赞我的用户列表, 结构 userId(be liked)->userId(like)
     */
    public static final String SHARE_USER_WHO_LIKED = LIKE_FUNCTION_PREFIX + "who_like_me:";
    /**
     * 点赞记录器, 结构 userId->shareId
     */
    public static final String SHARE_NEW_LIKE = LIKE_FUNCTION_PREFIX + "new_like_cnt";
    /**
     * 取消赞记录器, 结构 userId->shareId
     */
    public static final String SHARE_NEW_DISLIKE = LIKE_FUNCTION_PREFIX + "new_dislike_cnt";

    private final StringRedisTemplate template;
    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final ShareMapper shareMapper;

    public CachedLikeDaoImpl(StringRedisTemplate template, LikeMapper likeMapper,
                             ShareMapper shareMapper, LikeService likeService) {
        this.template = template;
        this.likeMapper = likeMapper;
        this.shareMapper = shareMapper;
        this.likeService = likeService;
    }

    // @PostConstruct
    public void loadCache() {
        // 加载记录被赞的用户列表, (key->value)为(shareId->Set<userId>)
        SetOperations<String, String> setOperations = this.template.opsForSet();
        List<LikeDo> likes = this.likeMapper.selectList(new LambdaQueryWrapper<LikeDo>()
                .select(LikeDo::getShareId, LikeDo::getUserId));
        likes.stream().collect(Collectors.groupingBy(LikeDo::getShareId,
                Collectors.mapping(l -> l.getUserId().toString(), Collectors.toSet())))
                .forEach((k, v) -> setOperations.add(SHARE_LIKED_USERS + k.toString(), v.toArray(String[]::new)));

        // 加载记录的总点赞数
        HashOperations<String, Object, Object> hashOperations = this.template.opsForHash();
        Map<String, String> totalLikes = shareMapper.selectList(new LambdaQueryWrapper<ShareDo>()
                .select(ShareDo::getShareId, ShareDo::getLikeCnt))
                .stream()
                .collect(Collectors.toMap(s -> s.getShareId().toString(),
                        s -> s.getLikeCnt().toString()));
        hashOperations.putAll(SHARE_TOTAL_LIKES, totalLikes);

        // 加载用户被赞的总数
        Map<String, String> userBeLiked = this.likeMapper.selectUserLikedCount()
                .stream()
                .collect(Collectors.toMap(m -> ((Integer) m.get("user_id")).toString(),
                        m -> ((Long) m.get("cnt")).toString()));
        hashOperations.putAll(SHARE_USER_LIKES, userBeLiked);

        // 加载赞我的用户
        this.likeMapper.selectUserWhoLikedMe().stream()
                .collect(Collectors.groupingBy(m -> ((Integer) m.get("beliked")).toString(),
                        Collectors.mapping(m -> ((Integer) m.get("like")).toString(), Collectors.toSet())))
                .forEach((k, v) -> setOperations.add(SHARE_USER_WHO_LIKED + k, v.toArray(String[]::new)));
    }

    // @PreDestroy
    public void destroyCache() {
        saveData();
        this.template.delete(this.template.keys(SHARE_LIKED_USERS + "*"));
        this.template.delete(SHARE_TOTAL_LIKES);
        this.template.delete(SHARE_USER_LIKES);
        this.template.delete(this.template.keys(SHARE_USER_WHO_LIKED + "*"));
        this.template.delete(SHARE_NEW_LIKE);
        this.template.delete(SHARE_NEW_DISLIKE);
    }

    // @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveData() {
        // 周期性将缓存写回数据库，抓住三个关键：点赞记录器、取消点赞记录器和记录总数变化量
        // 写点赞记录器
        HashOperations<String, Object, Object> hashOperations = this.template.opsForHash();
        List<Object> likeKeys = List.copyOf(hashOperations.keys(SHARE_NEW_LIKE));
        if (!likeKeys.isEmpty()) {
            List<Object> likeValues = hashOperations.multiGet(SHARE_NEW_LIKE, likeKeys);
            List<LikeDo> likes = new ArrayList<>(likeKeys.size());
            for (int i = 0; i < likeKeys.size(); i++) {
                likes.add(new LikeDo(null, Integer.parseInt((String) likeValues.get(i)),
                        Integer.parseInt((String) likeKeys.get(i)), false, 0));
            }
            this.likeService.saveBatch(likes, likeKeys.size());
        }
        // 清空记录器
        this.template.delete(SHARE_NEW_LIKE);

        // 写删除记录器
        LambdaQueryWrapper<LikeDo> deleteMapper = new LambdaQueryWrapper<>();
        List<Object> dislikeKeys = List.copyOf(hashOperations.keys(SHARE_NEW_DISLIKE));
        if (!dislikeKeys.isEmpty()) {
            List<Object> dislikeValues = hashOperations.multiGet(SHARE_NEW_DISLIKE, dislikeKeys);
            for (int i = 0; i < dislikeKeys.size(); i++) {
                deleteMapper.or().eq(LikeDo::getUserId, Integer.parseInt((String) dislikeValues.get(i)))
                        .eq(LikeDo::getShareId, Integer.parseInt((String) dislikeKeys.get(i)));
            }
            this.likeService.remove(deleteMapper);
        }
        // 清空记录器
        this.template.delete(SHARE_NEW_DISLIKE);

        // 写总数记录器
        List<Object> shareKeys = List.copyOf(hashOperations.keys(SHARE_TOTAL_LIKES));
        if (!shareKeys.isEmpty()) {
            List<Object> shareValues = hashOperations.multiGet(SHARE_TOTAL_LIKES, shareKeys);
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
        if (BooleanUtils.isTrue(this.template.hasKey(SHARE_LIKED_USERS + shareId.toString()))) {
            return BooleanUtils.isTrue(
                    this.template.opsForSet().isMember(SHARE_LIKED_USERS + shareId.toString(), userId.toString()));
        }
        // 不存在记录
        return false;
    }

    @Override
    public List<Boolean> checkIsLikeBatch(Integer userId, List<Integer> shareIds) {
        return shareIds.stream().map(s -> checkIsLike(userId, s)).collect(Collectors.toList());
    }

    @Override
    public void likeShare(Integer userId, Integer shareId) {
        if (!checkIsLike(userId, shareId)) {
            // 写入记录器
            this.template.opsForHash().put(SHARE_NEW_LIKE, shareId.toString(), userId.toString());
            // 写入记录被赞的用户列表
            this.template.opsForSet().add(SHARE_LIKED_USERS + shareId.toString(), userId.toString());
            // 写入记录被赞的总数
            this.template.opsForHash().increment(SHARE_TOTAL_LIKES, shareId.toString(), 1L);
            // 写入赞我的总数
            this.template.opsForHash().increment(SHARE_USER_LIKES, userId.toString(), 1L);
            // 写入赞我的用户
            this.template.opsForSet().add(SHARE_USER_WHO_LIKED, userId.toString());
        }
    }

    @Override
    public void dislikeShare(Integer userId, Integer shareId) {
        if (checkIsLike(userId, shareId)) {
            // 写入记录器
            this.template.opsForHash().put(SHARE_NEW_DISLIKE, shareId.toString(), userId.toString());
            // 移除记录被赞的用户列表
            this.template.opsForSet().remove(SHARE_LIKED_USERS + shareId.toString(), userId.toString());
            // 减少记录被赞数
            this.template.opsForHash().increment(SHARE_TOTAL_LIKES, shareId.toString(), -1L);
            // 减少赞我的总数
            this.template.opsForHash().increment(SHARE_USER_LIKES, userId.toString(), -1L);
            // TODO: 这里没有移除赞我的用户
        }
    }

    @Override
    public int getTotalLike(Integer userId) {
        return Optional.ofNullable((String) this.template.opsForHash()
                .get(SHARE_USER_LIKES, userId.toString())).map(Integer::parseInt)
                .orElse(0);
    }

    @Override
    public int getRecordLikes(Integer shareId) {
        return Optional.ofNullable((String) this.template.opsForHash().get(SHARE_TOTAL_LIKES, shareId.toString()))
                .map(Integer::parseInt)
                .orElse(0);
    }

    @Override
    public int getNewCount(Integer userId) {
        throw new UnsupportedOperationException("This class is deprecated, please use RedisLikeDaoImpl instead");
    }
}
