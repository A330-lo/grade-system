package com.scs.service;

import com.scs.entity.Student;
import com.scs.result.PageResult;
import com.scs.vo.StudentHomeVO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService {

    /**
     * 分页查询
     */
    PageResult<Student> page(Integer pageNum, Integer pageSize, Student student);

    /**
     * 获取所有学生
     */
    List<Student> list();

    /**
     * 根据ID获取
     */
    Student getById(Long id);

    /**
     * 根据学号获取
     */
    Student getByStudentNo(String studentNo);

    /**
     * 新增
     */
    void save(Student student);

    /**
     * 修改
     */
    void update(Student student);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);

    /**
     * 导入学生
     */
    void importData(MultipartFile file);

    /**
     * 导出学生
     */
    void exportData(HttpServletResponse response, Student student);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 学生首页统计
     */
    StudentHomeVO getHomeInfo(Long studentId);

    /**
     * 获取个人课表
     */
    List<Student> getSchedule(Long studentId, String semester);
}
