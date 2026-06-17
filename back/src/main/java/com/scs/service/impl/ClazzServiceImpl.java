package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Clazz;
import com.scs.exception.BusinessException;
import com.scs.mapper.ClazzMapper;
import com.scs.result.PageResult;
import com.scs.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 班级服务实现类
 */
@Service
public class ClazzServiceImpl implements ClazzService {

    @Resource
    private ClazzMapper clazzMapper;

    @Override
    public PageResult<Clazz> page(Integer pageNum, Integer pageSize, String clazzName, Long majorId) {
        Page<Clazz> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(clazzName)) {
            wrapper.like(Clazz::getClazzName, clazzName);
        }
        if (majorId != null) {
            wrapper.eq(Clazz::getMajorId, majorId);
        }
        wrapper.orderByDesc(Clazz::getId);
        page = clazzMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Clazz> listByMajorId(Long majorId) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getMajorId, majorId);
        wrapper.orderByAsc(Clazz::getClazzName);
        return clazzMapper.selectList(wrapper);
    }

    @Override
    public List<Clazz> list() {
        return clazzMapper.selectList(null);
    }

    @Override
    public Clazz getById(Long id) {
        return clazzMapper.selectById(id);
    }

    @Override
    public void save(Clazz clazz) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getClazzCode, clazz.getClazzCode());
        if (clazzMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("班级编码已存在");
        }
        clazzMapper.insert(clazz);
    }

    @Override
    public void update(Clazz clazz) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getClazzCode, clazz.getClazzCode())
                .ne(Clazz::getId, clazz.getId());
        if (clazzMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("班级编码已存在");
        }
        clazzMapper.updateById(clazz);
    }

    @Override
    public void delete(Long id) {
        clazzMapper.deleteById(id);
    }
}
