package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Admin;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.AdminService;
import com.scs.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@RequireAuth("admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Admin>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        PageResult<Admin> page = adminService.page(pageNum, pageSize, username);
        return Result.success(page);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return Result.success(admin);
    }

    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> getInfo() {
        Long userId = UserContext.getUserId();
        Admin admin = adminService.getById(userId);
        admin.setPassword(null);
        return Result.success(admin);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody Admin admin) {
        adminService.save(admin);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Admin admin) {
        adminService.update(admin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = UserContext.getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        adminService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
}
