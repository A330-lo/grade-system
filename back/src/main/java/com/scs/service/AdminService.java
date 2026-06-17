package com.scs.service;

import com.scs.entity.Admin;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 分页查询
     */
    PageResult<Admin> page(Integer pageNum, Integer pageSize, String username);

    /**
     * 根据ID获取
     */
    Admin getById(Long id);

    /**
     * 根据用户名获取
     */
    Admin getByUsername(String username);

    /**
     * 新增
     */
    void save(Admin admin);

    /**
     * 修改
     */
    void update(Admin admin);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
}
