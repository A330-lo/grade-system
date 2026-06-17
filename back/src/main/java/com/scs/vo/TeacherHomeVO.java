package com.scs.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 教师首页统计VO
 */
@Data
public class TeacherHomeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 授课门数
     */
    private Integer courseCount;

    /**
     * 学生总人数
     */
    private Integer studentCount;

    /**
     * 已录入成绩课程数
     */
    private Integer scoredCount;

    /**
     * 待录入成绩课程数
     */
    private Integer unscoredCount;

    /**
     * 本周课程数
     */
    private Integer weekCourses;

    /**
     * 今日课程数
     */
    private Integer todayCourses;
}
