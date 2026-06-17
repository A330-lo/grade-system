package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 学生表Mapper
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生列表（带关联信息）
     */
    Page<Student> selectStudentPage(Page<Student> page, @Param("student") Student student);
    /**
     * 根据ID查询学生信息(带班级名称)
     */
    Student selectStudentWithClazz(@Param("id") Long id);
}
