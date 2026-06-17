package com.scs.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学生首页统计VO
 */
@Data
public class StudentHomeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 已修课程数
     */
    private Integer totalCourses;

    /**
     * 已获得学分
     */
    private BigDecimal totalCredits;

    /**
     * 平均绩点
     */
    private BigDecimal avgGpa;

    /**
     * 及格课程数
     */
    private Integer passCount;

    /**
     * 不及格课程数
     */
    private Integer failCount;

    /**
     * 当前学期已选课程数
     */
    private Integer currentCourses;

    /**
     * 本周课程数
     */
    private Integer weekCourses;
}
