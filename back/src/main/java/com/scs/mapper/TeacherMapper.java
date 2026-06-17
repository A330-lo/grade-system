package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scs.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 教师表Mapper
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    /**
     * 根据ID查询教师信息(带部门名称)
     */
    Teacher selectTeacherWithDept(@Param("id") Long id);
}
