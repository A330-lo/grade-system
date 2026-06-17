package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Department;
import com.scs.exception.BusinessException;
import com.scs.mapper.DepartmentMapper;
import com.scs.result.PageResult;
import com.scs.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 系服务实现类
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public PageResult<Department> page(Integer pageNum, Integer pageSize, String deptName) {
        Page<Department> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deptName)) {
            wrapper.like(Department::getDeptName, deptName);
        }
        wrapper.orderByDesc(Department::getId);
        page = departmentMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Department> list() {
        return departmentMapper.selectList(null);
    }

    @Override
    public Department getById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public void save(Department department) {
        // 检查系编码是否已存在
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDeptCode, department.getDeptCode());
        if (departmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("系编码已存在");
        }
        departmentMapper.insert(department);
    }

    @Override
    public void update(Department department) {
        // 检查系编码是否已存在（排除自己）
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getDeptCode, department.getDeptCode())
                .ne(Department::getId, department.getId());
        if (departmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("系编码已存在");
        }
        departmentMapper.updateById(department);
    }

    @Override
    public void delete(Long id) {
        departmentMapper.deleteById(id);
    }
}
