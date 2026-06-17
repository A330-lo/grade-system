package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Teacher;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.TeacherService;
import com.scs.utils.UserContext;
import com.scs.vo.TeacherHomeVO;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    @RequireAuth("admin")
    public Result<PageResult<Teacher>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) Long deptId) {
        PageResult<Teacher> page = teacherService.page(pageNum, pageSize, teacherName, deptId);
        return Result.success(page);
    }

    /**
     * 获取所有教师（管理员）
     */
    @GetMapping("/list")
    @RequireAuth("admin")
    public Result<List<Teacher>> list() {
        List<Teacher> list = teacherService.list();
        return Result.success(list);
    }

    /**
     * 根据系ID获取教师列表
     */
    @GetMapping("/list-by-dept")
    @RequireAuth({"admin", "teacher", "student"})
    public Result<List<Teacher>> listByDeptId(@RequestParam Long deptId) {
        List<Teacher> list = teacherService.listByDeptId(deptId);
        return Result.success(list);
    }

    /**
     * 根据ID获取教师信息
     */
    @GetMapping("/{id}")
    @RequireAuth({"admin", "teacher"})
    public Result<Teacher> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return Result.success(teacher);
    }

    /**
     * 获取当前登录教师信息
     */
    @GetMapping("/info")
    @RequireAuth("teacher")
    public Result<Teacher> getInfo() {
        Long userId = UserContext.getUserId();
        Teacher teacher = teacherService.getById(userId);
        if (teacher != null) {
            teacher.setPassword(null);
        }
        return Result.success(teacher);
    }

    /**
     * 新增教师（管理员）
     */
    @PostMapping
    @RequireAuth("admin")
    public Result<Void> save(@RequestBody Teacher teacher) {
        teacherService.save(teacher);
        return Result.success();
    }

    /**
     * 修改教师信息（管理员）
     */
    @PutMapping
    @RequireAuth("admin")
    public Result<Void> update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return Result.success();
    }

    /**
     * 修改个人信息（教师）
     */
    @PutMapping("/profile")
    @RequireAuth("teacher")
    public Result<Void> updateProfile(@RequestBody Teacher teacher) {
        Long userId = UserContext.getUserId();
        teacher.setId(userId);
        teacher.setTeacherNo(null);
        teacher.setPassword(null);
        teacherService.update(teacher);
        return Result.success();
    }

    /**
     * 删除教师（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAuth("admin")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return Result.success();
    }

    /**
     * 修改密码（教师）
     */
    @PostMapping("/password")
    @RequireAuth("teacher")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = UserContext.getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        teacherService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 教师首页统计
     */
    @GetMapping("/home")
    @RequireAuth("teacher")
    public Result<TeacherHomeVO> getHomeInfo() {
        Long userId = UserContext.getUserId();
        TeacherHomeVO homeVO = teacherService.getHomeInfo(userId);
        return Result.success(homeVO);
    }
}
