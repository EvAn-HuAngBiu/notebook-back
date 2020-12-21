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
 * 标签表
 * </p>
 *
 * @author evan
 * @since 2020-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notebook_tag")
public class TagDo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签唯一ID
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;


}
