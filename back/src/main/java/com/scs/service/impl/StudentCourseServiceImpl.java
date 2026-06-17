package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Course;
import com.scs.entity.StudentCourse;
import com.scs.exception.BusinessException;
import com.scs.mapper.StudentCourseMapper;
import com.scs.result.PageResult;
import com.scs.service.CourseService;
import com.scs.service.StudentCourseService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课服务实现类
 */
@Service
public class StudentCourseServiceImpl implements StudentCourseService {

    @Resource
    private StudentCourseMapper studentCourseMapper;
    @Lazy
    @Resource
    private CourseService courseService;

    @Override
    public PageResult<StudentCourse> page(Integer pageNum, Integer pageSize, StudentCourse studentCourse, String studentNo, String courseName) {
        Page<StudentCourse> page = new Page<>(pageNum, pageSize);
        page = studentCourseMapper.selectStudentCoursePage(page, studentCourse, studentNo, courseName);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }
    @Override
    public StudentCourse getById(Long id) {
        return studentCourseMapper.selectById(id);
    }

    @Override
    public void save(StudentCourse studentCourse) {
        studentCourseMapper.insert(studentCourse);
    }

    @Override
    public void update(StudentCourse studentCourse) {
        studentCourseMapper.updateById(studentCourse);
    }

    @Override
    public void delete(Long id) {
        studentCourseMapper.deleteById(id);
    }

    @Override
    public void selectCourse(Long studentId, Long courseId) {
        // 检查是否已选
        if (isSelected(studentId, courseId)) {
            throw new BusinessException("您已选过此课程");
        }

        // 检查课程是否存在
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }

        // 检查课程人数是否已满
        int selectedCount = countByCourseId(courseId);
        if (course.getMaxStudent() != null && selectedCount >= course.getMaxStudent()) {
            throw new BusinessException("课程人数已满");
        }

        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(courseId);
        studentCourse.setSelectTime(LocalDateTime.now());
        studentCourse.setStatus(1);

        studentCourseMapper.insert(studentCourse);
    }

    @Override
    public void dropCourse(Long studentId, Long courseId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getStudentId, studentId)
                .eq(StudentCourse::getCourseId, courseId)
                .eq(StudentCourse::getStatus, 1);

        StudentCourse studentCourse = studentCourseMapper.selectOne(wrapper);
        if (studentCourse == null) {
            throw new BusinessException("您未选择此课程");
        }

        // 逻辑删除，修改状态
        studentCourse.setStatus(0);
        studentCourseMapper.updateById(studentCourse);
    }

    @Override
    public List<StudentCourse> getByStudentId(Long studentId) {
        return studentCourseMapper.selectStudentCoursesByStudentId(studentId);
    }

    @Override
    public List<StudentCourse> getByCourseId(Long courseId) {
        return studentCourseMapper.selectStudentCoursesByCourseId(courseId);
    }

    @Override
    public boolean isSelected(Long studentId, Long courseId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getStudentId, studentId)
                .eq(StudentCourse::getCourseId, courseId)
                .eq(StudentCourse::getStatus, 1);
        return studentCourseMapper.selectCount(wrapper) > 0;
    }

    /**
     * 统计课程已选人数
     */
    public int countByCourseId(Long courseId) {
        LambdaQueryWrapper<StudentCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentCourse::getCourseId, courseId)
                .eq(StudentCourse::getStatus, 1);
        return Math.toIntExact(studentCourseMapper.selectCount(wrapper));
    }
}
