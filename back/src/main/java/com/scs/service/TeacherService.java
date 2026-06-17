package com.scs.service;

import com.scs.entity.Teacher;
import com.scs.result.PageResult;
import com.scs.vo.TeacherHomeVO;
import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService {

    /**
     * 分页查询
     */
    PageResult<Teacher> page(Integer pageNum, Integer pageSize, String teacherName, Long deptId);

    /**
     * 获取所有教师
     */
    List<Teacher> list();

    /**
     * 根据系ID获取教师列表
     */
    List<Teacher> listByDeptId(Long deptId);

    /**
     * 根据ID获取
     */
    Teacher getById(Long id);

    /**
     * 根据工号获取
     */
    Teacher getByTeacherNo(String teacherNo);

    /**
     * 新增
     */
    void save(Teacher teacher);

    /**
     * 修改
     */
    void update(Teacher teacher);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 教师首页统计
     */
    TeacherHomeVO getHomeInfo(Long teacherId);
}
