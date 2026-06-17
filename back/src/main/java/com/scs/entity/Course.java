package com.scs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程表实体类
 */
@Data
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;

    /**
     * 学分
     */
    private BigDecimal credit;

    /**
     * 学时
     */
    private Integer classHours;

    /**
     * 开课学期
     */
    private String semester;

    /**
     * 课程类型：必修、选修
     */
    private String courseType;

    /**
     * 最大选课人数
     */
    private Integer maxStudent;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 授课教师ID
     */
    private Long teacherId;

    /**
     * 上课教室
     */
    private String classroom;

    /**
     * 星期几：1-7
     */
    private Integer weekDay;

    /**
     * 开始节次
     */
    private Integer startSection;

    /**
     * 结束节次
     */
    private Integer endSection;

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
     * 授课教师姓名（非表字段）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 所属系名称（非表字段）
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 已选人数（非表字段）
     */
    @TableField(exist = false)
    private Integer selectedCount;
    /**
     * 上课时间（非表字段，SQL计算得出如"周一 1-2节"）
     */
    @TableField(exist = false)
    private String schedule;
}
