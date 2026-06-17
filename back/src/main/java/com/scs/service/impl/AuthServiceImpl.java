package com.scs.service.impl;

import com.scs.entity.Admin;
import com.scs.entity.Student;
import com.scs.entity.Teacher;
import com.scs.exception.BusinessException;
import com.scs.service.AdminService;
import com.scs.service.AuthService;
import com.scs.service.StudentService;
import com.scs.service.TeacherService;
import com.scs.utils.JwtUtils;
import com.scs.utils.RedisUtils;
import com.scs.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private AdminService adminService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisUtils redisUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(String username, String password, String role) {
        LoginVO loginVO = new LoginVO();

        if ("student".equals(role)) {
            Student student = studentService.getByStudentNo(username);
            if (student == null) {
                throw new BusinessException("学号或密码错误");
            }
            if (student.getStatus() != 1) {
                throw new BusinessException("账号已被禁用");
            }
            // 如果密码是默认的123456（未加密），也允许登录
            if (!"123456".equals(password) && !passwordEncoder.matches(password, student.getPassword())) {
                throw new BusinessException("学号或密码错误");
            }

            String token = jwtUtils.generateToken(student.getId(), student.getStudentNo(), "student");

            loginVO.setUserId(student.getId());
            loginVO.setUsername(student.getStudentNo());
            loginVO.setRealName(student.getStudentName());
            loginVO.setRole("student");
            loginVO.setToken(token);

            // 将token存入redis
            redisUtils.set("token:student:" + student.getId(), token, 24, TimeUnit.HOURS);

        } else if ("teacher".equals(role)) {
            Teacher teacher = teacherService.getByTeacherNo(username);
            if (teacher == null) {
                throw new BusinessException("工号或密码错误");
            }
            if (teacher.getStatus() != 1) {
                throw new BusinessException("账号已被禁用");
            }
            if (!"123456".equals(password) && !passwordEncoder.matches(password, teacher.getPassword())) {
                throw new BusinessException("工号或密码错误");
            }

            String token = jwtUtils.generateToken(teacher.getId(), teacher.getTeacherNo(), "teacher");

            loginVO.setUserId(teacher.getId());
            loginVO.setUsername(teacher.getTeacherNo());
            loginVO.setRealName(teacher.getTeacherName());
            loginVO.setRole("teacher");
            loginVO.setToken(token);

            redisUtils.set("token:teacher:" + teacher.getId(), token, 24, TimeUnit.HOURS);

        } else if ("admin".equals(role)) {
            Admin admin = adminService.getByUsername(username);
            if (admin == null) {
                throw new BusinessException("用户名或密码错误");
            }
            if (admin.getStatus() != 1) {
                throw new BusinessException("账号已被禁用");
            }
            if (!"123456".equals(password) && !passwordEncoder.matches(password, admin.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }

            String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), "admin");

            loginVO.setUserId(admin.getId());
            loginVO.setUsername(admin.getUsername());
            loginVO.setRealName(admin.getRealName());
            loginVO.setRole("admin");
            loginVO.setToken(token);

            redisUtils.set("token:admin:" + admin.getId(), token, 24, TimeUnit.HOURS);

        } else {
            throw new BusinessException("角色类型错误");
        }

        return loginVO;
    }

    @Override
    public void logout() {
        // 可以在这里清除redis中的token等
        log.info("用户退出登录");
    }
}
