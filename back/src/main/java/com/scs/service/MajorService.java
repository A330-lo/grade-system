package com.scs.service;

import com.scs.entity.Major;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 专业服务接口
 */
public interface MajorService {

    /**
     * 分页查询
     */
    PageResult<Major> page(Integer pageNum, Integer pageSize, String majorName, Long deptId);

    /**
     * 根据系ID获取专业列表
     */
    List<Major> listByDeptId(Long deptId);

    /**
     * 获取所有专业
     */
    List<Major> list();

    /**
     * 根据ID获取
     */
    Major getById(Long id);

    /**
     * 新增
     */
    void save(Major major);

    /**
     * 修改
     */
    void update(Major major);

    /**
     * 删除
     */
    void delete(Long id);
}
