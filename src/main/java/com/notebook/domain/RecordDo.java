package com.notebook.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notebook.config.handler.JsonArrayTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 记录信息表
 * </p>
 *
 * @author evan
 * @since 2020-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "notebook_record", autoResultMap = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;

    /**
     * 记录创建者的用户ID
     */
    private Integer userId;

    /**
     * 记录对应的tag ID
     */
    private Integer tagId;

    /**
     * 记录对应的图片URL, 格式为JSON数组
     */
    @TableField(value = "pic_url", typeHandler = JsonArrayTypeHandler.class, jdbcType = JdbcType.OTHER)
    private List<String> picUrl;

    /**
     * 记录标题
     */
    private String recordTitle;

    /**
     * 记录的内容
     */
    private String recordText;

    /**
     * 记录类型 0:红榜 1:黑榜 2:种草
     */
    private Integer recordType;

    /**
     * 评分，总分为5
     */
    private Integer recordRate;

    /**
     * 添加时间
     */
    private LocalDateTime addTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime modifyTime;

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
