package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scs.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系表Mapper
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
