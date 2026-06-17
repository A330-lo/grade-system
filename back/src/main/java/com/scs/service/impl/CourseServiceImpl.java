package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Course;
import com.scs.entity.StudentCourse;
import com.scs.exception.BusinessException;
import com.scs.mapper.CourseMapper;
import com.scs.result.PageResult;
import com.scs.service.CourseService;
import com.scs.service.StudentCourseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private StudentCourseService studentCourseService;

    @Override
    public PageResult<Course> page(Integer pageNum, Integer pageSize, Course course) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        page = courseMapper.selectCoursePage(page, course);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Course> list() {
        return courseMapper.selectList(null);
    }

    @Override
    public List<Course> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.orderByDesc(Course::getId);
        List<Course> list = courseMapper.selectList(wrapper);

        // 补充选课人数
        for (Course course : list) {
            int count = studentCourseService.countByCourseId(course.getId());
            course.setSelectedCount(count);
        }

        return list;
    }

    @Override
    public List<Course> listBySemester(String semester) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getSemester, semester);
        wrapper.orderByDesc(Course::getId);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    public void save(Course course) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, course.getCourseCode());
        if (courseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("课程编码已存在");
        }
        courseMapper.insert(course);
    }

    @Override
    public void update(Course course) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, course.getCourseCode())
                .ne(Course::getId, course.getId());
        if (courseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("课程编码已存在");
        }
        courseMapper.updateById(course);
    }

    @Override
    public void delete(Long id) {
        courseMapper.deleteById(id);
    }

    @Override
    public PageResult<Course> getOptionalCourses(Integer pageNum, Integer pageSize, Long studentId, String courseName) {
        // 获取学生已选课程ID列表
        List<StudentCourse> selectedCourses = studentCourseService.getByStudentId(studentId);
        List<Long> selectedCourseIds = selectedCourses.stream()
                .filter(sc -> sc.getStatus() == 1)
                .map(StudentCourse::getCourseId)
                .collect(Collectors.toList());

        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(courseName)) {
            wrapper.like(Course::getCourseName, courseName);
        }
        // 排除已选课程
        if (!selectedCourseIds.isEmpty()) {
            wrapper.notIn(Course::getId, selectedCourseIds);
        }
        // 只显示选修课
        wrapper.eq(Course::getCourseType, "选修");
        wrapper.orderByDesc(Course::getId);

        page = courseMapper.selectPage(page, wrapper);

        // 补充已选人数等信息
        for (Course course : page.getRecords()) {
            // 简单计算已选人数
            LambdaQueryWrapper<StudentCourse> countWrapper = new LambdaQueryWrapper<>();
            countWrapper.eq(StudentCourse::getCourseId, course.getId());
            countWrapper.eq(StudentCourse::getStatus, 1);
            int count = studentCourseService.countByCourseId(course.getId());
            course.setSelectedCount(count);
        }

        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<String> getSemesters() {
        // 从课程表中获取所有不重复的学期
        List<Course> courses = courseMapper.selectList(null);
        return courses.stream()
                .map(Course::getSemester)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());
    }
}
