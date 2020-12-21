package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author evan
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_collect")
@NoArgsConstructor
@AllArgsConstructor
public class CollectDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标识收藏记录的ID
     */
    @TableId(value = "collect_id", type = IdType.AUTO)
    private Integer collectId;

    /**
     * 收藏的用户ID
     */
    private Integer userId;

    /**
     * 收藏的分享记录ID
     */
    private Integer shareId;

    /**
     * 逻辑删除
     */
    // @TableLogic
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;


}
