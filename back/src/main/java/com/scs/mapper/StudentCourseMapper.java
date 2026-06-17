package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 选课表Mapper
 */
@Mapper
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {

    /**
     * 分页查询选课列表(带关联信息)
     */
    Page<StudentCourse> selectStudentCoursePage(Page<StudentCourse> page, @Param("studentCourse") StudentCourse studentCourse, @Param("studentNo") String studentNo, @Param("courseName") String courseName);

    /**
     * 根据课程ID查询选课学生列表(带学生详细信息)
     */
    List<StudentCourse> selectStudentCoursesByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生ID查询选课列表(带课程详细信息)
     */
    List<StudentCourse> selectStudentCoursesByStudentId(@Param("studentId") Long studentId);
}

