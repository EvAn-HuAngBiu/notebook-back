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
 * 文件存储表
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_storage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "storage_id", type = IdType.AUTO)
    private Integer storageId;

    /**
     * 文件的唯一索引
     */
    private String keyStr;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 最后更新时间
     */
    private LocalDateTime modifiedTime;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @JsonIgnore
    private Boolean deleted;

    /**
     * 乐观锁字段
     */
    @Version
    @JsonIgnore
    private Integer version;
}
