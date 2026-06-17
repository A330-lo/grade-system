package com.scs.service;

import com.scs.entity.Course;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 分页查询
     */
    PageResult<Course> page(Integer pageNum, Integer pageSize, Course course);

    /**
     * 获取所有课程
     */
    List<Course> list();

    /**
     * 根据教师ID获取课程列表
     */
    List<Course> listByTeacherId(Long teacherId);

    /**
     * 根据学期获取课程列表
     */
    List<Course> listBySemester(String semester);

    /**
     * 根据ID获取
     */
    Course getById(Long id);

    /**
     * 新增
     */
    void save(Course course);

    /**
     * 修改
     */
    void update(Course course);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 获取可选课程列表（学生用）
     */
    PageResult<Course> getOptionalCourses(Integer pageNum, Integer pageSize, Long studentId, String courseName);

    /**
     * 获取学期列表
     */
    List<String> getSemesters();
}
