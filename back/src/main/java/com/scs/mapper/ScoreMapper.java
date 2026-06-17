package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩表Mapper
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 分页查询成绩列表（带关联信息）
     */
    Page<Score> selectScorePage(Page<Score> page, @Param("score") Score score);
    /**
     * 根据课程ID查询成绩列表（带学生信息）
     */
    List<Score> selectScoresByCourseId(@Param("courseId") Long courseId);
    /**
     * 分页查询学生成绩(带课程信息)
     */
    Page<Score> selectStudentScorePage(Page<Score> page, @Param("studentId") Long studentId, @Param("semester") String semester, @Param("courseName") String courseName);
}
