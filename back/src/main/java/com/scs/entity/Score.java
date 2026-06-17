package com.scs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩表实体类
 */
@Data
@TableName("score")
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 平时成绩
     */
    private BigDecimal usualScore;

    /**
     * 期末成绩
     */
    private BigDecimal finalScore;

    /**
     * 总成绩
     */
    private BigDecimal totalScore;

    /**
     * 绩点
     */
    private BigDecimal gpa;

    /**
     * 是否及格：1-是，0-否
     */
    private Integer isPass;

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
     * 学生信息（非表字段）
     */
    @TableField(exist = false)
    private Student student;

    /**
     * 课程信息（非表字段）
     */
    @TableField(exist = false)
    private Course course;
    /**
     * 状态：0-未发布(草稿)，1-已发布
     */
    private Integer status;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}