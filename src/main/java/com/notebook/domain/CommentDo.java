package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author evan
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_comment")
public class CommentDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    /**
     * 评论类型，0为对记录评论，1为回复评论
     */
    private Integer commentType;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论发布者的用户ID
     */
    private Integer userId;

    /**
     * 评论的记录ID
     */
    private Integer shareId;

    /**
     * 父评论ID
     */
    private Integer parentCommentId;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;


}
