package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Student;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.StudentService;
import com.scs.utils.UserContext;
import com.scs.vo.StudentHomeVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    @RequireAuth("admin")
    public Result<PageResult<Student>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Long clazzId) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setStudentName(studentName);
        student.setClazzId(clazzId);
        PageResult<Student> page = studentService.page(pageNum, pageSize, student);
        return Result.success(page);
    }

    /**
     * 获取所有学生（管理员）
     */
    @GetMapping("/list")
    @RequireAuth("admin")
    public Result<List<Student>> list() {
        List<Student> list = studentService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取学生信息
     */
    @GetMapping("/{id}")
    @RequireAuth({"admin", "student"})
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /**
     * 获取当前登录学生信息
     */
    @GetMapping("/info")
    @RequireAuth("student")
    public Result<Student> getInfo() {
        Long userId = UserContext.getUserId();
        Student student = studentService.getById(userId);
        student.setPassword(null);
        return Result.success(student);
    }

    /**
     * 新增学生（管理员）
     */
    @PostMapping
    @RequireAuth("admin")
    public Result<Void> save(@RequestBody Student student) {
        studentService.save(student);
        return Result.success();
    }

    /**
     * 修改学生信息（管理员）
     */
    @PutMapping
    @RequireAuth("admin")
    public Result<Void> update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    /**
     * 修改个人信息（学生）
     */
    @PutMapping("/profile")
    @RequireAuth("student")
    public Result<Void> updateProfile(@RequestBody Student student) {
        Long userId = UserContext.getUserId();
        student.setId(userId);
        // 不允许修改学号和密码
        student.setStudentNo(null);
        student.setPassword(null);
        studentService.update(student);
        return Result.success();
    }

    /**
     * 删除学生（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAuth("admin")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除学生（管理员）
     */
    @DeleteMapping("/batch")
    @RequireAuth("admin")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 导入学生（管理员）
     */
    @PostMapping("/import")
    @RequireAuth("admin")
    public Result<Void> importData(@RequestParam("file") MultipartFile file) {
        studentService.importData(file);
        return Result.success();
    }

    /**
     * 导出学生（管理员）
     */
    @GetMapping("/export")
    @RequireAuth("admin")
    public void exportData(HttpServletResponse response,
                           @RequestParam(required = false) String studentNo,
                           @RequestParam(required = false) String studentName) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setStudentName(studentName);
        studentService.exportData(response, student);
    }

    /**
     * 修改密码（学生）
     */
    @PostMapping("/password")
    @RequireAuth("student")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = UserContext.getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        studentService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 学生首页统计
     */
    @GetMapping("/home")
    @RequireAuth("student")
    public Result<StudentHomeVO> getHomeInfo() {
        Long userId = UserContext.getUserId();
        StudentHomeVO homeVO = studentService.getHomeInfo(userId);
        return Result.success(homeVO);
    }
}
