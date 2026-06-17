package com.scs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 学生选课成绩管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.scs.mapper")
@EnableCaching
public class StudentCourseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentCourseSystemApplication.class, args);
        System.out.println("==============================");
        System.out.println("  学生选课成绩管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080/api");
        System.out.println("==============================");
    }
}
