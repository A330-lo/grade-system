package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Department;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 系控制器
 */
@RestController
@RequestMapping("/department")
@RequireAuth("admin")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Department>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String deptName) {
        PageResult<Department> page = departmentService.page(pageNum, pageSize, deptName);
        return Result.success(page);
    }

    /**
     * 获取所有系
     */
    @GetMapping("/list")
    public Result<List<Department>> list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return Result.success(department);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody Department department) {
        departmentService.save(department);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Department department) {
        departmentService.update(department);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success();
    }
}
