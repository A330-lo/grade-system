-- ============================================================
-- 学生选课成绩管理系统 - 数据库初始化脚本
-- 数据库：student_course_system
-- 字符集：utf8mb4
-- 默认密码：123456（首次登录后建议修改）
-- 数据概览
-- 表	记录数	说明
-- department	15	15个系部
-- major	10	10个专业，关联到对应系
-- clazz	10	10个班级，关联到对应专业
-- admin	1	管理员 admin / 123456
-- teacher	5	5位教师，T2021001~T2021005
-- student	8	8位学生，分布在不同班级
-- course	8	8门课程，含必修/选修
-- student_course	15	15条选课记录
-- score	6	6条成绩记录（均为草稿状态）
-- 密码说明：所有账号初始密码均为 123456。登录代码中已兼容明文 123456 和 BCrypt 加密两种格式，首次登录后系统会自动加密存储。
-- ============================================================

CREATE DATABASE IF NOT EXISTS `student_course_system`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `student_course_system`;

-- ============================================================
-- 1. 系表
-- ============================================================
DROP TABLE IF EXISTS `score`;
DROP TABLE IF EXISTS `student_course`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `teacher`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `clazz`;
DROP TABLE IF EXISTS `major`;
DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `dept_name`   VARCHAR(50) NOT NULL                 COMMENT '系名称',
    `dept_code`   VARCHAR(20) NOT NULL                 COMMENT '系编码',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系表';

CREATE TABLE `major` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `major_name`  VARCHAR(50) NOT NULL                 COMMENT '专业名称',
    `major_code`  VARCHAR(20) NOT NULL                 COMMENT '专业编码',
    `dept_id`     BIGINT      NOT NULL                 COMMENT '所属系ID',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_major_code` (`major_code`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

CREATE TABLE `clazz` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `clazz_name`  VARCHAR(50) NOT NULL                 COMMENT '班级名称',
    `clazz_code`  VARCHAR(20) NOT NULL                 COMMENT '班级编码',
    `major_id`    BIGINT      NOT NULL                 COMMENT '所属专业ID',
    `grade`       VARCHAR(10) DEFAULT NULL             COMMENT '年级',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_clazz_code` (`clazz_code`),
    KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

CREATE TABLE `admin` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL                 COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL                 COMMENT '密码',
    `real_name`   VARCHAR(50)  DEFAULT NULL             COMMENT '真实姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
    `status`      TINYINT      DEFAULT 1                COMMENT '状态：1-正常，0-禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

CREATE TABLE `teacher` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `teacher_no`   VARCHAR(30)  NOT NULL                 COMMENT '工号',
    `teacher_name` VARCHAR(50)  NOT NULL                 COMMENT '教师姓名',
    `password`     VARCHAR(255) NOT NULL                 COMMENT '密码',
    `gender`       VARCHAR(5)   DEFAULT NULL             COMMENT '性别',
    `title`        VARCHAR(30)  DEFAULT NULL             COMMENT '职称',
    `phone`        VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
    `email`        VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
    `dept_id`      BIGINT       DEFAULT NULL             COMMENT '所属系ID',
    `status`       TINYINT      DEFAULT 1                COMMENT '状态：1-正常，0-禁用',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

CREATE TABLE `student` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `student_no`      VARCHAR(30)  NOT NULL                 COMMENT '学号',
    `student_name`    VARCHAR(50)  NOT NULL                 COMMENT '学生姓名',
    `password`        VARCHAR(255) NOT NULL                 COMMENT '密码',
    `gender`          VARCHAR(5)   DEFAULT NULL             COMMENT '性别',
    `birthday`        DATE         DEFAULT NULL             COMMENT '生日',
    `phone`           VARCHAR(20)  DEFAULT NULL             COMMENT '手机号',
    `email`           VARCHAR(100) DEFAULT NULL             COMMENT '邮箱',
    `clazz_id`        BIGINT       DEFAULT NULL             COMMENT '班级ID',
    `enrollment_date` DATE         DEFAULT NULL             COMMENT '入学日期',
    `status`          TINYINT      DEFAULT 1                COMMENT '状态：1-正常，0-禁用',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_clazz_id` (`clazz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

CREATE TABLE `course` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `course_name`   VARCHAR(100)  NOT NULL                 COMMENT '课程名称',
    `course_code`   VARCHAR(30)   NOT NULL                 COMMENT '课程编码',
    `credit`        DECIMAL(3,1)  DEFAULT 0.0              COMMENT '学分',
    `class_hours`   INT           DEFAULT 0                COMMENT '学时',
    `semester`      VARCHAR(20)   DEFAULT NULL             COMMENT '开课学期',
    `course_type`   VARCHAR(10)   DEFAULT NULL             COMMENT '课程类型：必修/选修',
    `max_student`   INT           DEFAULT NULL             COMMENT '最大选课人数',
    `description`   VARCHAR(500)  DEFAULT NULL             COMMENT '课程描述',
    `teacher_id`    BIGINT        DEFAULT NULL             COMMENT '授课教师ID',
    `classroom`     VARCHAR(50)   DEFAULT NULL             COMMENT '上课教室',
    `week_day`      INT           DEFAULT NULL             COMMENT '星期几：1-7',
    `start_section` INT           DEFAULT NULL             COMMENT '开始节次',
    `end_section`   INT           DEFAULT NULL             COMMENT '结束节次',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_code` (`course_code`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

CREATE TABLE `student_course` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `student_id`  BIGINT   NOT NULL                 COMMENT '学生ID',
    `course_id`   BIGINT   NOT NULL                 COMMENT '课程ID',
    `select_time` DATETIME DEFAULT NULL             COMMENT '选课时间',
    `status`      TINYINT  DEFAULT 1                COMMENT '状态：1-已选，0-已退选',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

CREATE TABLE `score` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `student_id`  BIGINT       NOT NULL                 COMMENT '学生ID',
    `course_id`   BIGINT       NOT NULL                 COMMENT '课程ID',
    `usual_score` DECIMAL(5,2) DEFAULT NULL             COMMENT '平时成绩(0-100)',
    `final_score` DECIMAL(5,2) DEFAULT NULL             COMMENT '期末成绩(0-100)',
    `total_score` DECIMAL(5,2) DEFAULT NULL             COMMENT '总成绩',
    `gpa`         DECIMAL(3,2) DEFAULT NULL             COMMENT '绩点',
    `is_pass`     TINYINT      DEFAULT 0                COMMENT '是否及格：1-是，0-否',
    `status`      TINYINT      DEFAULT 0                COMMENT '状态：0-草稿，1-已发布',
    `deleted`     TINYINT      DEFAULT 0                COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- ============================================================
-- 初始化数据：系部
-- ============================================================
INSERT INTO `department` (`id`, `dept_name`, `dept_code`) VALUES
(1,  '计算机科学与技术学院', 'CS'),
(2,  '数学与统计学院',       'MATH'),
(3,  '外国语学院',           'FL'),
(4,  '电子信息工程学院',     'EE'),
(5,  '机械工程学院',         'ME'),
(6,  '土木工程学院',         'CE'),
(7,  '材料科学与工程学院',   'MSE'),
(8,  '环境科学与工程学院',   'ESE'),
(9,  '艺术设计学院',         'AD'),
(10, '经济管理学院',         'EM'),
(11, '物理学院',             'PHY'),
(12, '化学学院',             'CHEM'),
(13, '生物工程学院',         'BE'),
(14, '新闻传播学院',         'JC'),
(15, '法学院',               'LAW');

-- ============================================================
-- 初始化数据：专业
-- ============================================================
INSERT INTO `major` (`id`, `major_name`, `major_code`, `dept_id`) VALUES
(1,  '计算机科学与技术',   'CS01', 1),
(2,  '软件工程',           'CS02', 1),
(3,  '数据科学与大数据',   'CS03', 1),
(4,  '数学与应用数学',     'MATH01', 2),
(5,  '英语',               'FL01', 3),
(6,  '电子信息工程',       'EE01', 4),
(7,  '机械设计制造',       'ME01', 5),
(8,  '土木工程',           'CE01', 6),
(9,  '工商管理',           'EM01', 10),
(10, '法学',               'LAW01', 15);

-- ============================================================
-- 初始化数据：班级
-- ============================================================
INSERT INTO `clazz` (`id`, `clazz_name`, `clazz_code`, `major_id`, `grade`) VALUES
(1, '计科2101班', 'CS2101', 1, '2021'),
(2, '计科2102班', 'CS2102', 1, '2021'),
(3, '软件2101班', 'SE2101', 2, '2021'),
(4, '数据2101班', 'DS2101', 3, '2021'),
(5, '数学2101班', 'MA2101', 4, '2021'),
(6, '英语2101班', 'EN2101', 5, '2021'),
(7, '电信2101班', 'EE2101', 6, '2021'),
(8, '机械2101班', 'ME2101', 7, '2021'),
(9, '土木2101班', 'CE2101', 8, '2021'),
(10, '工商2101班', 'BA2101', 9, '2021');

-- ============================================================
-- 初始化数据：管理员（默认密码 123456）
-- ============================================================
INSERT INTO `admin` (`id`, `username`, `password`, `real_name`, `status`) VALUES
(1, 'admin', '123456', '系统管理员', 1);

-- ============================================================
-- 初始化数据：教师（默认密码 123456）
-- ============================================================
INSERT INTO `teacher` (`id`, `teacher_no`, `teacher_name`, `password`, `gender`, `title`, `phone`, `email`, `dept_id`, `status`) VALUES
(1, 'T2021001', '张教授', '123456', '男', '教授',     '13800001001', 'zhang@scs.edu.cn', 1,  1),
(2, 'T2021002', '李副教授', '123456', '女', '副教授',  '13800001002', 'li@scs.edu.cn',   1,  1),
(3, 'T2021003', '王讲师', '123456', '男', '讲师',     '13800001003', 'wang@scs.edu.cn', 2,  1),
(4, 'T2021004', '赵教授', '123456', '女', '教授',     '13800001004', 'zhao@scs.edu.cn', 4,  1),
(5, 'T2021005', '陈讲师', '123456', '男', '讲师',     '13800001005', 'chen@scs.edu.cn', 5,  1);

-- ============================================================
-- 初始化数据：学生（默认密码 123456）
-- ============================================================
INSERT INTO `student` (`id`, `student_no`, `student_name`, `password`, `gender`, `birthday`, `phone`, `email`, `clazz_id`, `enrollment_date`, `status`) VALUES
(1,  '20210101001', '张三', '123456', '男', '2002-05-15', '13900001001', 'zhangsan@scs.edu.cn',   1, '2021-09-01', 1),
(2,  '20210101002', '李四', '123456', '女', '2002-08-20', '13900001002', 'lisi@scs.edu.cn',       1, '2021-09-01', 1),
(3,  '20210101003', '王五', '123456', '男', '2003-01-10', '13900001003', 'wangwu@scs.edu.cn',     2, '2021-09-01', 1),
(4,  '20210102001', '赵六', '123456', '女', '2002-11-05', '13900001004', 'zhaoliu@scs.edu.cn',    3, '2021-09-01', 1),
(5,  '20210103001', '孙七', '123456', '男', '2002-06-18', '13900001005', 'sunqi@scs.edu.cn',      4, '2021-09-01', 1),
(6,  '20210201001', '周八', '123456', '女', '2002-09-22', '13900001006', 'zhouba@scs.edu.cn',     5, '2021-09-01', 1),
(7,  '20210301001', '吴九', '123456', '男', '2003-03-30', '13900001007', 'wujiu@scs.edu.cn',      6, '2021-09-01', 1),
(8,  '20210401001', '郑十', '123456', '女', '2002-12-12', '13900001008', 'zhengshi@scs.edu.cn',  7, '2021-09-01', 1);

-- ============================================================
-- 初始化数据：课程
-- ============================================================
INSERT INTO `course` (`id`, `course_name`, `course_code`, `credit`, `class_hours`, `semester`, `course_type`, `max_student`, `description`, `teacher_id`, `classroom`, `week_day`, `start_section`, `end_section`) VALUES
(1,  'Java程序设计',     'CS101', 4.0, 64, '2024-2025-1', '必修', 60, '面向对象编程基础，涵盖Java核心语法与常用框架',      1, '教1-301', 1, 1, 2),
(2,  '数据结构与算法',   'CS102', 3.5, 56, '2024-2025-1', '必修', 60, '线性表、树、图等经典数据结构及其算法分析',            1, '教1-302', 2, 3, 4),
(3,  '数据库原理',       'CS201', 3.0, 48, '2024-2025-1', '必修', 50, '关系型数据库设计、SQL语言与事务管理',                2, '教2-201', 3, 1, 2),
(4,  '操作系统',         'CS202', 3.5, 56, '2024-2025-1', '必修', 50, '进程管理、内存管理、文件系统与I/O管理',              2, '教2-202', 4, 3, 4),
(5,  '高等数学A',        'MATH101', 5.0, 80, '2024-2025-1', '必修', 80, '微积分、级数、常微分方程',                          3, '教3-101', 1, 1, 3),
(6,  '线性代数',         'MATH102', 3.0, 48, '2024-2025-1', '必修', 80, '矩阵理论、向量空间与线性变换',                      3, '教3-102', 3, 5, 6),
(7,  '电路分析',         'EE101', 3.0, 48, '2024-2025-1', '必修', 40, '电路基本定律、分析方法与定理',                      4, '教4-101', 2, 1, 2),
(8,  'Python程序设计',   'CS103', 3.0, 48, '2024-2025-1', '选修', 40, 'Python基础语法与数据分析入门',                      1, '教1-303', 5, 1, 2);

-- ============================================================
-- 初始化数据：选课记录
-- ============================================================
INSERT INTO `student_course` (`id`, `student_id`, `course_id`, `select_time`, `status`) VALUES
(1,  1, 1, '2024-09-01 10:00:00', 1),
(2,  1, 2, '2024-09-01 10:05:00', 1),
(3,  1, 5, '2024-09-01 10:10:00', 1),
(4,  2, 1, '2024-09-01 10:15:00', 1),
(5,  2, 3, '2024-09-01 10:20:00', 1),
(6,  2, 5, '2024-09-01 10:25:00', 1),
(7,  3, 2, '2024-09-01 10:30:00', 1),
(8,  3, 4, '2024-09-01 10:35:00', 1),
(9,  4, 1, '2024-09-01 10:40:00', 1),
(10, 4, 8, '2024-09-01 10:45:00', 1),
(11, 5, 3, '2024-09-01 10:50:00', 1),
(12, 5, 5, '2024-09-01 10:55:00', 1),
(13, 6, 6, '2024-09-01 11:00:00', 1),
(14, 7, 7, '2024-09-01 11:05:00', 1),
(15, 8, 7, '2024-09-01 11:10:00', 1);

-- ============================================================
-- 初始化数据：成绩（草稿状态，待教师录入）
-- ============================================================
INSERT INTO `score` (`id`, `student_id`, `course_id`, `usual_score`, `final_score`, `total_score`, `gpa`, `is_pass`, `status`, `deleted`) VALUES
(1, 1, 1, 85.00, 90.00, 88.50, 3.70, 1, 0, 0),
(2, 1, 2, 78.00, 82.00, 80.80, 3.00, 1, 0, 0),
(3, 2, 1, 92.00, 88.00, 89.20, 3.70, 1, 0, 0),
(4, 2, 3, 75.00, 70.00, 71.50, 2.30, 1, 0, 0),
(5, 3, 2, 60.00, 55.00, 56.50, 0.00, 0, 0, 0),
(6, 4, 1, 95.00, 93.00, 93.60, 4.00, 1, 0, 0);