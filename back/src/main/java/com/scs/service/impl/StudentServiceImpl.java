package com.scs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scs.entity.Score;
import com.scs.entity.Student;
import com.scs.entity.StudentCourse;
import com.scs.exception.BusinessException;
import com.scs.mapper.StudentMapper;
import com.scs.result.PageResult;
import com.scs.service.ScoreService;
import com.scs.service.StudentCourseService;
import com.scs.service.StudentService;
import com.scs.vo.StudentHomeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

/**
 * 学生服务实现类
 */
@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private StudentCourseService studentCourseService;

    @Resource
    private ScoreService scoreService;

    @Value("${scs.defaultPassword:123456}")
    private String defaultPassword;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<Student> page(Integer pageNum, Integer pageSize, Student student) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        page = studentMapper.selectStudentPage(page, student);
        return PageResult.of(page.getTotal(), page.getRecords(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<Student> list() {
        return studentMapper.selectList(null);
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectStudentWithClazz(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);
        return studentMapper.selectOne(wrapper);
    }

    @Override
    public void save(Student student) {
        // 检查学号是否已存在
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo());
        if (studentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("学号已存在");
        }
        // 设置默认密码
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(defaultPassword));
        } else {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        if (student.getStatus() == null) {
            student.setStatus(1);
        }
        studentMapper.insert(student);
    }

    @Override
    public void update(Student student) {
        // 检查学号是否已存在（排除自己）
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, student.getStudentNo())
                .ne(Student::getId, student.getId());
        if (studentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("学号已存在");
        }
        // 不修改密码
        student.setPassword(null);
        studentMapper.updateById(student);
    }

    @Override
    public void delete(Long id) {
        studentMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        studentMapper.deleteBatchIds(ids);
    }

    @Override
    public void importData(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Student student = new Student();
                student.setStudentNo(getCellValue(row.getCell(0)));
                student.setStudentName(getCellValue(row.getCell(1)));
                student.setGender(getCellValue(row.getCell(2)));
                student.setPhone(getCellValue(row.getCell(3)));
                student.setEmail(getCellValue(row.getCell(4)));
                // 班级需要根据名称查找ID，这里简化处理，默认班级ID为1
                student.setClazzId(1L);
                student.setStatus(1);
                student.setPassword(passwordEncoder.encode(defaultPassword));

                try {
                    save(student);
                } catch (Exception e) {
                    log.warn("导入学生失败，学号：{}，原因：{}", student.getStudentNo(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("导入学生数据失败", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportData(HttpServletResponse response, Student student) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "性别", "手机号", "邮箱", "班级", "入学日期", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 获取数据
            List<Student> students = studentMapper.selectList(null);
            int rowNum = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getStudentNo());
                row.createCell(1).setCellValue(s.getStudentName());
                row.createCell(2).setCellValue(s.getGender() != null ? s.getGender() : "");
                row.createCell(3).setCellValue(s.getPhone() != null ? s.getPhone() : "");
                row.createCell(4).setCellValue(s.getEmail() != null ? s.getEmail() : "");
                row.createCell(5).setCellValue(s.getClazzName() != null ? s.getClazzName() : "");
                row.createCell(6).setCellValue(s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "");
                row.createCell(7).setCellValue(s.getStatus() == 1 ? "正常" : "禁用");
            }

            // 设置响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("学生信息.xlsx", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (Exception e) {
            log.error("导出学生数据失败", e);
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        student.setPassword(passwordEncoder.encode(newPassword));
        studentMapper.updateById(student);
    }

    @Override
    public StudentHomeVO getHomeInfo(Long studentId) {
        StudentHomeVO vo = new StudentHomeVO();

        // 获取已选课程数
        List<StudentCourse> courses = studentCourseService.getByStudentId(studentId);
        vo.setCurrentCourses((int) courses.stream().filter(c -> c.getStatus() == 1).count());

        // 获取成绩列表
        List<Score> scores = scoreService.getByStudentId(studentId);
        vo.setTotalCourses(scores.size());

        // 计算总学分
        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalGpa = BigDecimal.ZERO;
        int passCount = 0;
        int failCount = 0;

        for (Score score : scores) {
            if (score.getCourse() != null && score.getCourse().getCredit() != null) {
                if (score.getIsPass() == 1) {
                    totalCredits = totalCredits.add(score.getCourse().getCredit());
                    passCount++;
                } else {
                    failCount++;
                }
            }
            if (score.getGpa() != null) {
                totalGpa = totalGpa.add(score.getGpa());
            }
        }

        vo.setTotalCredits(totalCredits);
        vo.setPassCount(passCount);
        vo.setFailCount(failCount);

        if (!scores.isEmpty()) {
            vo.setAvgGpa(totalGpa.divide(BigDecimal.valueOf(scores.size()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            vo.setAvgGpa(BigDecimal.ZERO);
        }

        // 本周课程数（简化处理，直接用已选课程数）
        vo.setWeekCourses(vo.getCurrentCourses());

        return vo;
    }

    @Override
    public List<Student> getSchedule(Long studentId, String semester) {
        // 简化实现，返回空列表，实际应查询课程表
        return null;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
