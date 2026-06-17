package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Course;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.CourseService;
import com.scs.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    @RequireAuth({"admin", "student", "teacher"})
    public Result<PageResult<Course>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String courseType) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseCode(courseCode);
        course.setTeacherId(teacherId);
        course.setSemester(semester);
        course.setCourseType(courseType);
        PageResult<Course> page = courseService.page(pageNum, pageSize, course);
        return Result.success(page);
    }

    /**
     * 获取所有课程
     */
    @GetMapping("/list")
    @RequireAuth({"admin", "student", "teacher"})
    public Result<List<Course>> list() {
        List<Course> list = courseService.list();
        return Result.success(list);
    }

    /**
     * 根据教师ID获取课程列表
     */
    @GetMapping("/list-by-teacher")
    @RequireAuth({"admin", "teacher"})
    public Result<List<Course>> listByTeacherId(@RequestParam(required = false) Long teacherId) {
        if (teacherId == null) {
            teacherId = UserContext.getUserId();
        }
        List<Course> list = courseService.listByTeacherId(teacherId);
        return Result.success(list);
    }

    /**
     * 根据学期获取课程列表
     */
    @GetMapping("/list-by-semester")
    @RequireAuth({"admin", "student", "teacher"})
    public Result<List<Course>> listBySemester(@RequestParam String semester) {
        List<Course> list = courseService.listBySemester(semester);
        return Result.success(list);
    }

    /**
     * 根据ID获取课程详情
     */
    @GetMapping("/{id}")
    @RequireAuth({"admin", "student", "teacher"})
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    /**
     * 新增课程（管理员）
     */
    @PostMapping
    @RequireAuth("admin")
    public Result<Void> save(@RequestBody Course course) {
        courseService.save(course);
        return Result.success();
    }

    /**
     * 修改课程（管理员）
     */
    @PutMapping
    @RequireAuth("admin")
    public Result<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return Result.success();
    }

    /**
     * 删除课程（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAuth("admin")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }

    /**
     * 获取可选课程列表（学生）
     */
    @GetMapping("/optional")
    @RequireAuth("student")
    public Result<PageResult<Course>> getOptionalCourses(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName) {
        Long studentId = UserContext.getUserId();
        PageResult<Course> page = courseService.getOptionalCourses(pageNum, pageSize, studentId, courseName);
        return Result.success(page);
    }

    /**
     * 获取学期列表
     */
    @GetMapping("/semesters")
    @RequireAuth({"admin", "student", "teacher"})
    public Result<List<String>> getSemesters() {
        List<String> semesters = courseService.getSemesters();
        return Result.success(semesters);
    }
}
