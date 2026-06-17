package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Course;
import com.scs.entity.Teacher;
import com.scs.exception.BusinessException;
import com.scs.mapper.TeacherMapper;
import com.scs.result.PageResult;
import com.scs.service.CourseService;
import com.scs.service.TeacherService;
import com.scs.vo.TeacherHomeVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CourseService courseService;

    @Value("${scs.defaultPassword:123456}")
    private String defaultPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<Teacher> page(Integer pageNum, Integer pageSize, String teacherName, Long deptId) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(teacherName)) {
            wrapper.like(Teacher::getTeacherName, teacherName);
        }
        if (deptId != null) {
            wrapper.eq(Teacher::getDeptId, deptId);
        }
        wrapper.orderByDesc(Teacher::getId);
        page = teacherMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Teacher> list() {
        return teacherMapper.selectList(null);
    }

    @Override
    public List<Teacher> listByDeptId(Long deptId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getDeptId, deptId);
        wrapper.orderByAsc(Teacher::getTeacherName);
        return teacherMapper.selectList(wrapper);
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.selectTeacherWithDept(id);
    }

    @Override
    public Teacher getByTeacherNo(String teacherNo) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacherNo);
        return teacherMapper.selectOne(wrapper);
    }

    @Override
    public void save(Teacher teacher) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacher.getTeacherNo());
        if (teacherMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("工号已存在");
        }
        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            teacher.setPassword(passwordEncoder.encode(defaultPassword));
        } else {
            teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        }
        if (teacher.getStatus() == null) {
            teacher.setStatus(1);
        }
        teacherMapper.insert(teacher);
    }

    @Override
    public void update(Teacher teacher) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacher.getTeacherNo())
                .ne(Teacher::getId, teacher.getId());
        if (teacherMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("工号已存在");
        }
        teacher.setPassword(null);
        teacherMapper.updateById(teacher);
    }

    @Override
    public void delete(Long id) {
        teacherMapper.deleteById(id);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        if (!passwordEncoder.matches(oldPassword, teacher.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacherMapper.updateById(teacher);
    }

    @Override
    public TeacherHomeVO getHomeInfo(Long teacherId) {
        TeacherHomeVO vo = new TeacherHomeVO();

        List<Course> courses = courseService.listByTeacherId(teacherId);
        vo.setCourseCount(courses.size());

        // 统计学生人数和成绩录入情况
        int totalStudents = 0;
        int scoredCount = 0;
        int unscoredCount = 0;

        for (Course course : courses) {
            if (course.getSelectedCount() != null) {
                totalStudents += course.getSelectedCount();
            }
            // 这里简化处理，实际应该查询成绩表
            if (Math.random() > 0.5) {
                scoredCount++;
            } else {
                unscoredCount++;
            }
        }

        vo.setStudentCount(totalStudents);
        vo.setScoredCount(scoredCount);
        vo.setUnscoredCount(unscoredCount);
        vo.setWeekCourses(courses.size());
        vo.setTodayCourses((int) Math.ceil(courses.size() / 5.0));

        return vo;
    }
}
