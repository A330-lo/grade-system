package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Major;
import com.scs.exception.BusinessException;
import com.scs.mapper.MajorMapper;
import com.scs.result.PageResult;
import com.scs.service.MajorService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 专业服务实现类
 */
@Service
public class MajorServiceImpl implements MajorService {

    @Resource
    private MajorMapper majorMapper;

    @Override
    public PageResult<Major> page(Integer pageNum, Integer pageSize, String majorName, Long deptId) {
        Page<Major> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(majorName)) {
            wrapper.like(Major::getMajorName, majorName);
        }
        if (deptId != null) {
            wrapper.eq(Major::getDeptId, deptId);
        }
        wrapper.orderByDesc(Major::getId);
        page = majorMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Major> listByDeptId(Long deptId) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getDeptId, deptId);
        wrapper.orderByAsc(Major::getMajorName);
        return majorMapper.selectList(wrapper);
    }

    @Override
    public List<Major> list() {
        return majorMapper.selectList(null);
    }

    @Override
    public Major getById(Long id) {
        return majorMapper.selectById(id);
    }

    @Override
    public void save(Major major) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getMajorCode, major.getMajorCode());
        if (majorMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("专业编码已存在");
        }
        majorMapper.insert(major);
    }

    @Override
    public void update(Major major) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Major::getMajorCode, major.getMajorCode())
                .ne(Major::getId, major.getId());
        if (majorMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("专业编码已存在");
        }
        majorMapper.updateById(major);
    }

    @Override
    public void delete(Long id) {
        majorMapper.deleteById(id);
    }
}
