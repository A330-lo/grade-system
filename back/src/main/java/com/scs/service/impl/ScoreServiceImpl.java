package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Score;
import com.scs.exception.BusinessException;
import com.scs.mapper.ScoreMapper;
import com.scs.result.PageResult;
import com.scs.service.ScoreService;
import com.scs.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource
    private ScoreMapper scoreMapper;

    @Override
    public PageResult<Score> page(Integer pageNum, Integer pageSize, Score score) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        page = scoreMapper.selectScorePage(page, score);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public Score getById(Long id) {
        return scoreMapper.selectById(id);
    }

    @Override
    public void save(Score score) {
        validateScore(score);
        calculateScoreAndGpa(score);
        scoreMapper.insert(score);
    }

    @Override
    public void update(Score score) {
        Score existScore = scoreMapper.selectById(score.getId());
        if (existScore == null) {
            throw new BusinessException("成绩记录不存在");
        }
        if (existScore.getStatus() != null && existScore.getStatus() == 1
                && !"admin".equals(UserContext.getRole())) {
            throw new BusinessException("已发布的成绩不可修改，请先退回");
        }
        validateScore(score);
        calculateScoreAndGpa(score);
        score.setStatus(existScore.getStatus());
        scoreMapper.updateById(score);
    }

    @Override
    public void delete(Long id) {
        scoreMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<Score> scores) {
        for (Score score : scores) {
            validateScore(score);
            calculateScoreAndGpa(score);

            // 检查是否已存在成绩
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Score::getStudentId, score.getStudentId())
                    .eq(Score::getCourseId, score.getCourseId());
            Score existScore = scoreMapper.selectOne(wrapper);

            if (existScore != null) {
                if (existScore.getStatus() != null && existScore.getStatus() == 1
                        && !"admin".equals(UserContext.getRole())) {
                    throw new BusinessException("已发布的成绩不可修改，请先退回");
                }
                score.setId(existScore.getId());
                score.setStatus(existScore.getStatus() != null ? existScore.getStatus() : 0);
                scoreMapper.updateById(score);
            } else {
                // 新增，默认草稿
                score.setStatus(0);
                scoreMapper.insert(score);
            }
        }
    }

    @Override
    public List<Score> getByStudentId(Long studentId) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId, studentId);
        return scoreMapper.selectList(wrapper);
    }

    @Override
    public List<Score> getByCourseId(Long courseId) {
        return scoreMapper.selectScoresByCourseId(courseId);
    }

    @Override
    public Double calculateGpa(Double totalScore) {
        if (totalScore == null) {
            return 0.0;
        }
        if (totalScore >= 90) return 4.0;
        if (totalScore >= 85) return 3.7;
        if (totalScore >= 82) return 3.3;
        if (totalScore >= 78) return 3.0;
        if (totalScore >= 75) return 2.7;
        if (totalScore >= 72) return 2.3;
        if (totalScore >= 68) return 2.0;
        if (totalScore >= 64) return 1.5;
        if (totalScore >= 60) return 1.0;
        return 0.0;
    }

    /**
     * 校验成绩范围：平时成绩和期末成绩必须在0-100之间
     */
    private void validateScore(Score score) {
        BigDecimal usualScore = score.getUsualScore();
        BigDecimal finalScore = score.getFinalScore();
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = new BigDecimal("100");

        if (usualScore != null && (usualScore.compareTo(min) < 0 || usualScore.compareTo(max) > 0)) {
            throw new BusinessException("平时成绩必须在0-100之间");
        }
        if (finalScore != null && (finalScore.compareTo(min) < 0 || finalScore.compareTo(max) > 0)) {
            throw new BusinessException("期末成绩必须在0-100之间");
        }
        if (usualScore == null && finalScore == null) {
            throw new BusinessException("平时成绩和期末成绩不能同时为空");
        }
    }

    /**
     * 计算总成绩和绩点
     */
    private void calculateScoreAndGpa(Score score) {
        BigDecimal usualScore = score.getUsualScore() != null ? score.getUsualScore() : BigDecimal.ZERO;
        BigDecimal finalScore = score.getFinalScore() != null ? score.getFinalScore() : BigDecimal.ZERO;

        // 总成绩 = 平时成绩 * 0.3 + 期末成绩 * 0.7
        BigDecimal totalScore = usualScore.multiply(new BigDecimal("0.3"))
                .add(finalScore.multiply(new BigDecimal("0.7")));
        score.setTotalScore(totalScore);

        // 计算绩点
        double gpa = calculateGpa(totalScore.doubleValue());
        score.setGpa(BigDecimal.valueOf(gpa));

        // 是否及格
        score.setIsPass(totalScore.compareTo(new BigDecimal("60")) >= 0 ? 1 : 0);
    }

    @Override
    public PageResult<Score> pageByStudentId(Integer pageNum, Integer pageSize, Long studentId, String semester, String courseName) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        page = scoreMapper.selectStudentScorePage(page, studentId, semester, courseName);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public void publish(Long id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        score.setStatus(1);
        scoreMapper.updateById(score);
    }

    @Override
    public void publishBatch(List<Long> ids) {
        for (Long id : ids) {
            publish(id);
        }
    }

    @Override
    public void reject(Long id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        score.setStatus(0);
        scoreMapper.updateById(score);
    }

    @Override
    public void rejectBatch(List<Long> ids) {
        for (Long id : ids) {
            reject(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        scoreMapper.deleteBatchIds(ids);
    }
}