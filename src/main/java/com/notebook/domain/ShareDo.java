package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author evan
 * @since 2020-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_share")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享表的唯一标识ID
     */
    @TableId(value = "share_id", type = IdType.AUTO)
    private Integer shareId;

    /**
     * 创建者的用户ID
     */
    private Integer userId;

    /**
     * 分享表对应的tag id
     */
    private Integer tagId;

    /**
     * 点赞数
     */
    private Integer likeCnt;

    /**
     * 收藏数
     */
    private Integer collectCnt;

    /**
     * 添加时间
     */
    private LocalDateTime addTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @JsonIgnore
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    @JsonIgnore
    private Integer version;
}
