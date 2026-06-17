package com.scs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scs.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员表Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
