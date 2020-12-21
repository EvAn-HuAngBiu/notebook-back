package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 点赞表
 * </p>
 *
 * @author evan
 * @since 2020-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_like")
@AllArgsConstructor
@NoArgsConstructor
public class LikeDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞表主键
     */
    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    /**
     * 点赞用户
     */
    private Integer userId;

    /**
     * 点赞的记录ID
     */
    private Integer shareId;

    /**
     * 逻辑删除
     */
    // @TableLogic
    @JsonIgnore
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    @JsonIgnore
    private Integer version;
}
