package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;

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
 * @since 2020-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_notify")
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "notify_id", type = IdType.AUTO)
    private Integer notifyId;

    /**
     * 对应的评论ID
     */
    private Integer commentId;

    /**
     * 通知用户ID
     */
    private Integer notifyUserId;

    /**
     * 阅读状态：0为未读，1为已读
     */
    private Boolean readType;

    /**
     * 记录添加时间
     * */
    private LocalDateTime addTime;

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
