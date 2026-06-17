package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Admin;
import com.scs.exception.BusinessException;
import com.scs.mapper.AdminMapper;
import com.scs.result.PageResult;
import com.scs.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Value("${scs.defaultPassword:123456}")
    private String defaultPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<Admin> page(Integer pageNum, Integer pageSize, String username) {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(Admin::getUsername, username);
        }
        wrapper.orderByDesc(Admin::getId);
        page = adminMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }

    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        return adminMapper.selectOne(wrapper);
    }

    @Override
    public void save(Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(defaultPassword));
        } else {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        if (admin.getStatus() == null) {
            admin.setStatus(1);
        }
        adminMapper.insert(admin);
    }

    @Override
    public void update(Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername())
                .ne(Admin::getId, admin.getId());
        if (adminMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        admin.setPassword(null);
        adminMapper.updateById(admin);
    }

    @Override
    public void delete(Long id) {
        adminMapper.deleteById(id);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);
    }
}
