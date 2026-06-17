package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.StudentCourse;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.StudentCourseService;
import com.scs.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 选课控制器
 */
@RestController
@RequestMapping("/student-course")
public class StudentCourseController {

    @Resource
    private StudentCourseService studentCourseService;

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    @RequireAuth("admin")
    public Result<PageResult<StudentCourse>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String courseName) {
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(courseId);
        studentCourse.setStatus(status);
        PageResult<StudentCourse> page = studentCourseService.page(pageNum, pageSize, studentCourse, studentNo, courseName);
        return Result.success(page);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    @RequireAuth("admin")
    public Result<StudentCourse> getById(@PathVariable Long id) {
        StudentCourse studentCourse = studentCourseService.getById(id);
        return Result.success(studentCourse);
    }

    /**
     * 新增（管理员）
     */
    @PostMapping
    @RequireAuth("admin")
    public Result<Void> save(@RequestBody StudentCourse studentCourse) {
        studentCourseService.save(studentCourse);
        return Result.success();
    }

    /**
     * 修改（管理员）
     */
    @PutMapping
    @RequireAuth("admin")
    public Result<Void> update(@RequestBody StudentCourse studentCourse) {
        studentCourseService.update(studentCourse);
        return Result.success();
    }

    /**
     * 删除（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAuth("admin")
    public Result<Void> delete(@PathVariable Long id) {
        studentCourseService.delete(id);
        return Result.success();
    }

    /**
     * 学生选课
     */
    @PostMapping("/select")
    @RequireAuth("student")
    public Result<Void> selectCourse(@RequestParam Long courseId) {
        Long studentId = UserContext.getUserId();
        studentCourseService.selectCourse(studentId, courseId);
        return Result.success();
    }

    /**
     * 学生退课
     */
    @PostMapping("/drop")
    @RequireAuth("student")
    public Result<Void> dropCourse(@RequestParam Long courseId) {
        Long studentId = UserContext.getUserId();
        studentCourseService.dropCourse(studentId, courseId);
        return Result.success();
    }

    /**
     * 获取学生已选课程列表
     */
    @GetMapping("/my-courses")
    @RequireAuth("student")
    public Result<List<StudentCourse>> getMyCourses() {
        Long studentId = UserContext.getUserId();
        List<StudentCourse> list = studentCourseService.getByStudentId(studentId);
        return Result.success(list);
    }

    /**
     * 获取课程的选课学生列表（教师）
     */
    @GetMapping("/course-students")
    @RequireAuth({"admin", "teacher"})
    public Result<List<StudentCourse>> getCourseStudents(@RequestParam Long courseId) {
        List<StudentCourse> list = studentCourseService.getByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 检查学生是否已选某课程
     */
    @GetMapping("/is-selected")
    @RequireAuth("student")
    public Result<Boolean> isSelected(@RequestParam Long courseId) {
        Long studentId = UserContext.getUserId();
        boolean selected = studentCourseService.isSelected(studentId, courseId);
        return Result.success(selected);
    }
}
