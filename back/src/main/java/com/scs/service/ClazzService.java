package com.scs.service;

import com.scs.entity.Clazz;
import com.scs.result.PageResult;
import java.util.List;

/**
 * 班级服务接口
 */
public interface ClazzService {

    /**
     * 分页查询
     */
    PageResult<Clazz> page(Integer pageNum, Integer pageSize, String clazzName, Long majorId);

    /**
     * 根据专业ID获取班级列表
     */
    List<Clazz> listByMajorId(Long majorId);

    /**
     * 获取所有班级
     */
    List<Clazz> list();

    /**
     * 根据ID获取
     */
    Clazz getById(Long id);

    /**
     * 新增
     */
    void save(Clazz clazz);

    /**
     * 修改
     */
    void update(Clazz clazz);

    /**
     * 删除
     */
    void delete(Long id);
}
