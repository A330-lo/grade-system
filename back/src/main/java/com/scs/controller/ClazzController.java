package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Clazz;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.ClazzService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 班级控制器
 */
@RestController
@RequestMapping("/clazz")
@RequireAuth("admin")
public class ClazzController {

    @Resource
    private ClazzService clazzService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageResult<Clazz>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String clazzName,
            @RequestParam(required = false) Long majorId) {
        PageResult<Clazz> page = clazzService.page(pageNum, pageSize, clazzName, majorId);
        return Result.success(page);
    }

    /**
     * 根据专业ID获取班级列表
     */
    @GetMapping("/list-by-major")
    public Result<List<Clazz>> listByMajorId(@RequestParam Long majorId) {
        List<Clazz> list = clazzService.listByMajorId(majorId);
        return Result.success(list);
    }

    /**
     * 获取所有班级
     */
    @GetMapping("/list")
    public Result<List<Clazz>> list() {
        List<Clazz> list = clazzService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    public Result<Clazz> getById(@PathVariable Long id) {
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<Void> save(@RequestBody Clazz clazz) {
        clazzService.save(clazz);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping
    public Result<Void> update(@RequestBody Clazz clazz) {
        clazzService.update(clazz);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return Result.success();
    }
}
