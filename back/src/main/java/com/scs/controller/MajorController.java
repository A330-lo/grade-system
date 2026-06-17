package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Major;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.MajorService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 专业控制器
 */
@RestController
@RequestMapping("/major")
@RequireAuth("admin")
public class MajorController {

    @Resource
    private MajorService majorService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Major>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String majorName,
            @RequestParam(required = false) Long deptId) {
        PageResult<Major> page = majorService.page(pageNum, pageSize, majorName, deptId);
        return Result.success(page);
    }

    /**
     * 根据系ID获取专业列表
     */
    @GetMapping("/list-by-dept")
    public Result<List<Major>> listByDeptId(@RequestParam Long deptId) {
        List<Major> list = majorService.listByDeptId(deptId);
        return Result.success(list);
    }

    /**
     * 获取所有专业
     */
    @GetMapping("/list")
    public Result<List<Major>> list() {
        List<Major> list = majorService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    public Result<Major> getById(@PathVariable Long id) {
        Major major = majorService.getById(id);
        return Result.success(major);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody Major major) {
        majorService.save(major);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Major major) {
        majorService.update(major);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        majorService.delete(id);
        return Result.success();
    }
}
