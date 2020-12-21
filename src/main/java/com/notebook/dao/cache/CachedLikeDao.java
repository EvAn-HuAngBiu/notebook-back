package com.notebook.dao.cache;

import java.util.List;

/**
 * Project: notebook
 * File: CachedLikeDao
 *
 * @author evan
 * @date 2020/11/13
 */
public interface CachedLikeDao {
    boolean checkIsLike(Integer userId, Integer shareId);

    List<Boolean> checkIsLikeBatch(Integer userId, List<Integer> shareIds);

    void likeShare(Integer userId, Integer shareId);

    void dislikeShare(Integer userId, Integer shareId);

    int getTotalLike(Integer userId);

    int getRecordLikes(Integer shareId);

    int getNewCount(Integer userId);
}
