package com.scs.service;

import com.scs.entity.Department;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 系服务接口
 */
public interface DepartmentService {

    /**
     * 分页查询
     */
    PageResult<Department> page(Integer pageNum, Integer pageSize, String deptName);

    /**
     * 获取所有系
     */
    List<Department> list();

    /**
     * 根据ID获取
     */
    Department getById(Long id);

    /**
     * 新增
     */
    void save(Department department);

    /**
     * 修改
     */
    void update(Department department);

    /**
     * 删除
     */
    void delete(Long id);
}
