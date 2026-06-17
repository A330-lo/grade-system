package com.scs.controller;

import com.scs.annotation.RequireAuth;
import com.scs.entity.Score;
import com.scs.result.PageResult;
import com.scs.result.Result;
import com.scs.service.ScoreService;
import com.scs.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    /**
     * 分页查询（管理员）
     */
    @GetMapping("/page")
    @RequireAuth("admin")
    public Result<PageResult<Score>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer isPass,
            @RequestParam(required = false) Integer status) {
        Score score = new Score();
        score.setStudentId(studentId);
        score.setCourseId(courseId);
        score.setIsPass(isPass);
        score.setStatus(status);
        PageResult<Score> page = scoreService.page(pageNum, pageSize, score);
        return Result.success(page);
    }

    /**
     * 根据ID获取
     */
    @GetMapping("/{id}")
    @RequireAuth({"admin", "teacher", "student"})
    public Result<Score> getById(@PathVariable Long id) {
        Score score = scoreService.getById(id);
        return Result.success(score);
    }

    /**
     * 新增（管理员）
     */
    @PostMapping
    @RequireAuth("admin")
    public Result<Void> save(@RequestBody Score score) {
        scoreService.save(score);
        return Result.success();
    }

    /**
     * 修改（管理员/教师）
     */
    @PutMapping
    @RequireAuth({"admin", "teacher"})
    public Result<Void> update(@RequestBody Score score) {
        scoreService.update(score);
        return Result.success();
    }

    /**
     * 删除（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAuth("admin")
    public Result<Void> delete(@PathVariable Long id) {
        scoreService.delete(id);
        return Result.success();
    }

    /**
     * 批量录入成绩（教师）
     */
    @PostMapping("/batch")
    @RequireAuth({"admin", "teacher"})
    public Result<Void> batchSave(@RequestBody List<Score> scores) {
        scoreService.batchSave(scores);
        return Result.success();
    }

    /**
     * 获取学生成绩列表(学生)
     */
    @GetMapping("/my-scores")
    @RequireAuth("student")
    public Result<PageResult<Score>> getMyScores(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String courseName) {
        Long studentId = UserContext.getUserId();
        PageResult<Score> page = scoreService.pageByStudentId(pageNum, pageSize, studentId, semester, courseName);
        return Result.success(page);
    }

    /**
     * 获取课程成绩列表（教师）
     */
    @GetMapping("/course-scores")
    @RequireAuth({"admin", "teacher"})
    public Result<List<Score>> getCourseScores(@RequestParam Long courseId) {
        List<Score> list = scoreService.getByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 发布成绩（管理员审核通过）
     */
    @PutMapping("/publish/{id}")
    @RequireAuth("admin")
    public Result<Void> publish(@PathVariable Long id) {
        scoreService.publish(id);
        return Result.success();
    }

    /**
     * 批量发布成绩
     */
    @PutMapping("/publish-batch")
    @RequireAuth("admin")
    public Result<Void> publishBatch(@RequestBody List<Long> ids) {
        scoreService.publishBatch(ids);
        return Result.success();
    }

    /**
     * 退回成绩（退回草稿）
     */
    @PutMapping("/reject/{id}")
    @RequireAuth("admin")
    public Result<Void> reject(@PathVariable Long id) {
        scoreService.reject(id);
        return Result.success();
    }

    /**
     * 批量退回成绩
     */
    @PutMapping("/reject-batch")
    @RequireAuth("admin")
    public Result<Void> rejectBatch(@RequestBody List<Long> ids) {
        scoreService.rejectBatch(ids);
        return Result.success();
    }

    /**
     * 批量删除成绩
     */
    @DeleteMapping("/batch")
    @RequireAuth("admin")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        scoreService.deleteBatch(ids);
        return Result.success();
    }
}