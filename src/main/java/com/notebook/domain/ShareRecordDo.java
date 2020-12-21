package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * @since 2020-11-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_share_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareRecordDo implements Serializable {

    private static final long serialVersionUID = 1L;

    public ShareRecordDo(Integer shareId, Integer recordId) {
        this.shareId = shareId;
        this.recordId = recordId;
    }

    /**
     * 分享记录表唯一标识ID
     */
    @TableId(value = "share_record_id", type = IdType.AUTO)
    private Integer shareRecordId;

    /**
     * 对应分享表中的父ID
     */
    private Integer shareId;

    /**
     * 对应记录表中的记录ID
     */
    private Integer recordId;

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
