package com.scs.service;

import com.scs.entity.StudentCourse;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 选课服务接口
 */
public interface StudentCourseService {

    /**
     * 分页查询
     */
    /**
     * 分页查询
     */
    PageResult<StudentCourse> page(Integer pageNum, Integer pageSize, StudentCourse studentCourse, String studentNo, String courseName);

    /**
     * 根据ID获取
     */
    StudentCourse getById(Long id);

    /**
     * 新增
     */
    void save(StudentCourse studentCourse);

    /**
     * 修改
     */
    void update(StudentCourse studentCourse);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 学生选课
     */
    void selectCourse(Long studentId, Long courseId);

    /**
     * 学生退课
     */
    void dropCourse(Long studentId, Long courseId);

    /**
     * 获取学生已选课程列表
     */
    List<StudentCourse> getByStudentId(Long studentId);

    /**
     * 获取课程的选课学生列表
     */
    List<StudentCourse> getByCourseId(Long courseId);

    /**
     * 检查学生是否已选某课程
     */
    boolean isSelected(Long studentId, Long courseId);

    int countByCourseId(Long id);
}
