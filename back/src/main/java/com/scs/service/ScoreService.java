package com.scs.service;

import com.scs.entity.Score;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 成绩服务接口
 */
public interface ScoreService {

    /**
     * 分页查询
     */
    PageResult<Score> page(Integer pageNum, Integer pageSize, Score score);

    /**
     * 根据ID获取
     */
    Score getById(Long id);

    /**
     * 新增
     */
    void save(Score score);

    /**
     * 修改
     */
    void update(Score score);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 批量录入成绩
     */
    void batchSave(List<Score> scores);

    /**
     * 获取学生成绩列表
     */
    List<Score> getByStudentId(Long studentId);

    /**
     * 获取课程成绩列表
     */
    List<Score> getByCourseId(Long courseId);

    /**
     * 计算绩点
     */
    Double calculateGpa(Double totalScore);

    /**
     * 分页查询学生成绩
     */
    PageResult<Score> pageByStudentId(Integer pageNum, Integer pageSize, Long studentId, String semester, String courseName);

    /**
     * 发布成绩（管理员审核通过）
     */
    void publish(Long id);

    /**
     * 批量发布成绩
     */
    void publishBatch(List<Long> ids);

    /**
     * 退回成绩（退回草稿）
     */
    void reject(Long id);

    /**
     * 批量退回成绩
     */
    void rejectBatch(List<Long> ids);

    /**
     * 批量删除成绩
     */
    void deleteBatch(List<Long> ids);
}