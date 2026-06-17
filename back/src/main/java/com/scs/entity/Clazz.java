package com.scs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级表实体类
 */
@Data
@TableName("clazz")
public class Clazz implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    private String clazzName;

    /**
     * 班级编码
     */
    private String clazzCode;

    /**
     * 所属专业ID
     */
    private Long majorId;

    /**
     * 年级
     */
    private String grade;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 所属专业名称（非表字段）
     */
    @TableField(exist = false)
    private String majorName;

    /**
     * 所属系名称（非表字段）
     */
    @TableField(exist = false)
    private String deptName;
}
