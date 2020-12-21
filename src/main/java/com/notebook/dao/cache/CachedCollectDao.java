package com.notebook.dao.cache;

import java.util.List;
import java.util.Set;

/**
 * Project: notebook
 * File: CachedCollectDao
 *
 * @author evan
 * @date 2020/12/5
 */
public interface CachedCollectDao {
    boolean checkIsCollect(Integer userId, Integer shareId);

    List<Boolean> checkIsCollectBatch(Integer userId, List<Integer> shareIds);

    void collectShare(Integer userId, Integer shareId);

    void cancelCollectShare(Integer userId, Integer shareId);

    Set<Integer> getUserCollects(Integer userId);

    int getRecordCollects(Integer shareId);

    long getUserTotalCollects(Integer userId);

    List<Integer> getPagedUserCollects(Integer userId, Integer page, Integer size);
}
