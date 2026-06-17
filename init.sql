-- 学生选课成绩管理系统数据库脚本
-- 数据库版本: MySQL 8.0+
-- 创建时间: 2024

-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_course_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_course_system;

-- 1. 系表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '系名称',
    dept_code VARCHAR(50) NOT NULL COMMENT '系编码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_dept_code (dept_code),
    INDEX idx_dept_name (dept_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系表';

-- 2. 专业表
CREATE TABLE IF NOT EXISTS major (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    major_code VARCHAR(50) NOT NULL COMMENT '专业编码',
    dept_id BIGINT NOT NULL COMMENT '所属系ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_major_code (major_code),
    INDEX idx_dept_id (dept_id),
    INDEX idx_major_name (major_name),
    FOREIGN KEY (dept_id) REFERENCES department(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- 3. 班级表
CREATE TABLE IF NOT EXISTS clazz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    clazz_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    clazz_code VARCHAR(50) NOT NULL COMMENT '班级编码',
    major_id BIGINT NOT NULL COMMENT '所属专业ID',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_clazz_code (clazz_code),
    INDEX idx_major_id (major_id),
    INDEX idx_clazz_name (clazz_name),
    FOREIGN KEY (major_id) REFERENCES major(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 4. 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_no VARCHAR(50) NOT NULL COMMENT '学号',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    password VARCHAR(255) NOT NULL DEFAULT '123456' COMMENT '密码',
    gender VARCHAR(10) COMMENT '性别',
    birthday DATE COMMENT '生日',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    clazz_id BIGINT NOT NULL COMMENT '班级ID',
    enrollment_date DATE COMMENT '入学日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_no (student_no),
    INDEX idx_clazz_id (clazz_id),
    INDEX idx_student_name (student_name),
    FOREIGN KEY (clazz_id) REFERENCES clazz(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 5. 教师表
CREATE TABLE IF NOT EXISTS teacher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    teacher_no VARCHAR(50) NOT NULL COMMENT '工号',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    password VARCHAR(255) NOT NULL DEFAULT '123456' COMMENT '密码',
    gender VARCHAR(10) COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    dept_id BIGINT NOT NULL COMMENT '所属系ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_teacher_no (teacher_no),
    INDEX idx_dept_id (dept_id),
    INDEX idx_teacher_name (teacher_name),
    FOREIGN KEY (dept_id) REFERENCES department(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 6. 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 7. 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) NOT NULL COMMENT '课程编码',
    credit DECIMAL(3,1) NOT NULL COMMENT '学分',
    class_hours INT NOT NULL COMMENT '学时',
    semester VARCHAR(50) NOT NULL COMMENT '开课学期',
    course_type VARCHAR(20) DEFAULT '必修' COMMENT '课程类型：必修、选修',
    max_student INT DEFAULT 50 COMMENT '最大选课人数',
    description TEXT COMMENT '课程描述',
    teacher_id BIGINT COMMENT '授课教师ID',
    classroom VARCHAR(50) COMMENT '上课教室',
    week_day INT COMMENT '星期几：1-7',
    start_section INT COMMENT '开始节次',
    end_section INT COMMENT '结束节次',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_course_code (course_code),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_semester (semester),
    INDEX idx_course_name (course_name),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 8. 选课表
CREATE TABLE IF NOT EXISTS student_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    select_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已选，0-已退选',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

-- 9. 成绩表
CREATE TABLE IF NOT EXISTS score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    usual_score DECIMAL(5,2) DEFAULT 0 COMMENT '平时成绩',
    final_score DECIMAL(5,2) DEFAULT 0 COMMENT '期末成绩',
    total_score DECIMAL(5,2) DEFAULT 0 COMMENT '总成绩',
    gpa DECIMAL(3,2) DEFAULT 0 COMMENT '绩点',
    is_pass TINYINT DEFAULT 0 COMMENT '是否及格：1-是，0-否',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course_score (student_id, course_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 初始化数据

-- 系数据
INSERT INTO department (dept_name, dept_code) VALUES
('计算机科学与技术系', 'CS'),
('软件工程系', 'SE'),
('信息管理系', 'IM'),
('电子工程系', 'EE'),
('数学系', 'MA');

-- 专业数据
INSERT INTO major (major_name, major_code, dept_id) VALUES
('计算机科学与技术', 'CS001', 1),
('人工智能', 'AI001', 1),
('软件工程', 'SE001', 2),
('软件工程（嵌入式方向）', 'SE002', 2),
('信息管理与信息系统', 'IM001', 3),
('电子商务', 'EC001', 3),
('电子信息工程', 'EE001', 4),
('通信工程', 'CE001', 4),
('数学与应用数学', 'MA001', 5),
('数据科学与大数据技术', 'DS001', 5);

-- 班级数据
INSERT INTO clazz (clazz_name, clazz_code, major_id, grade) VALUES
('计算机1班', 'CS202401', 1, '2024'),
('计算机2班', 'CS202402', 1, '2024'),
('人工智能1班', 'AI202401', 2, '2024'),
('软件1班', 'SE202401', 3, '2024'),
('软件2班', 'SE202402', 3, '2024'),
('信管1班', 'IM202401', 5, '2024'),
('电商1班', 'EC202401', 6, '2024'),
('电信1班', 'EE202401', 7, '2024'),
('通信1班', 'CE202401', 8, '2024'),
('数学1班', 'MA202401', 9, '2024'),
('大数据1班', 'DS202401', 10, '2024'),
('计算机3班', 'CS202301', 1, '2023'),
('软件3班', 'SE202301', 3, '2023');

-- 管理员数据（密码：admin123，实际使用时需要BCrypt加密）
INSERT INTO admin (username, password, real_name, phone, email, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '系统管理员', '13800138000', 'admin@example.com', 1);

-- 教师数据
INSERT INTO teacher (teacher_no, teacher_name, password, gender, title, phone, email, dept_id, status) VALUES
('T001', '张教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '教授', '13900139001', 'zhang@example.com', 1, 1),
('T002', '李副教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '副教授', '13900139002', 'li@example.com', 1, 1),
('T003', '王讲师', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '讲师', '13900139003', 'wang@example.com', 2, 1),
('T004', '刘教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '教授', '13900139004', 'liu@example.com', 2, 1),
('T005', '陈副教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '副教授', '13900139005', 'chen@example.com', 3, 1),
('T006', '赵讲师', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '讲师', '13900139006', 'zhao@example.com', 4, 1),
('T007', '孙教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '教授', '13900139007', 'sun@example.com', 5, 1),
('T008', '周副教授', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '副教授', '13900139008', 'zhou@example.com', 5, 1);

-- 学生数据（实际使用时密码需要BCrypt加密）
INSERT INTO student (student_no, student_name, password, gender, birthday, phone, email, clazz_id, enrollment_date, status) VALUES
('2024001', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-01-15', '13800000001', 'zhangsan@example.com', 1, '2024-09-01', 1),
('2024002', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '2006-02-20', '13800000002', 'lisi@example.com', 1, '2024-09-01', 1),
('2024003', '王五', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-03-10', '13800000003', 'wangwu@example.com', 1, '2024-09-01', 1),
('2024004', '赵六', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '2006-04-05', '13800000004', 'zhaoliu@example.com', 2, '2024-09-01', 1),
('2024005', '钱七', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-05-12', '13800000005', 'qianqi@example.com', 2, '2024-09-01', 1),
('2024006', '孙八', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-06-18', '13800000006', 'sunba@example.com', 3, '2024-09-01', 1),
('2024007', '周九', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '2006-07-22', '13800000007', 'zhoujiu@example.com', 3, '2024-09-01', 1),
('2024008', '吴十', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-08-30', '13800000008', 'wushi@example.com', 4, '2024-09-01', 1),
('2024009', '郑十一', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '2006-09-14', '13800000009', 'zheng11@example.com', 4, '2024-09-01', 1),
('2024010', '冯十二', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-10-08', '13800000010', 'feng12@example.com', 5, '2024-09-01', 1),
('2024011', '陈十三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '女', '2006-11-25', '13800000011', 'chen13@example.com', 6, '2024-09-01', 1),
('2024012', '褚十四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6e6xq', '男', '2006-12-03', '13800000012', 'chu14@example.com', 7, '2024-09-01', 1);

-- 课程数据
INSERT INTO course (course_name, course_code, credit, class_hours, semester, course_type, max_student, description, teacher_id, classroom, week_day, start_section, end_section) VALUES
('高等数学', 'MA101', 4.0, 64, '2024-2025-1', '必修', 100, '高等数学是理工科各专业的重要基础课程', 7, '教学楼A101', 1, 1, 2),
('大学英语', 'EN101', 3.0, 48, '2024-2025-1', '必修', 60, '培养学生的英语综合应用能力', 8, '教学楼B201', 2, 3, 4),
('计算机导论', 'CS101', 3.0, 48, '2024-2025-1', '必修', 80, '计算机学科入门课程，介绍计算机基础知识', 1, '教学楼C301', 3, 1, 2),
('C语言程序设计', 'CS102', 4.0, 64, '2024-2025-1', '必修', 60, 'C语言程序设计基础', 2, '实验楼D101', 4, 3, 4),
('线性代数', 'MA102', 3.0, 48, '2024-2025-1', '必修', 80, '线性代数基础理论', 7, '教学楼A102', 5, 1, 2),
('数据结构', 'CS201', 4.0, 64, '2024-2025-1', '必修', 60, '数据结构与算法基础', 1, '教学楼C302', 1, 3, 4),
('Java程序设计', 'SE201', 3.0, 48, '2024-2025-2', '必修', 60, 'Java编程语言基础与应用', 3, '实验楼D201', 2, 1, 2),
('数据库原理', 'SE202', 3.5, 56, '2024-2025-2', '必修', 50, '数据库系统原理与设计', 4, '教学楼C303', 3, 3, 4),
('操作系统', 'CS202', 3.5, 56, '2024-2025-2', '必修', 50, '操作系统基本原理', 2, '教学楼C304', 4, 1, 2),
('计算机网络', 'CS301', 4.0, 64, '2024-2025-2', '必修', 50, '计算机网络原理与应用', 1, '教学楼C401', 5, 3, 4),
('软件工程', 'SE301', 3.0, 48, '2025-2026-1', '必修', 50, '软件工程理论与实践', 4, '教学楼C402', 1, 5, 6),
('人工智能导论', 'AI201', 2.0, 32, '2024-2025-2', '选修', 40, '人工智能基础概念与应用', 3, '教学楼C501', 3, 5, 6),
('Python程序设计', 'SE101', 2.0, 32, '2024-2025-1', '选修', 40, 'Python编程语言入门', 5, '实验楼D301', 2, 5, 6),
('网页设计基础', 'IM101', 2.0, 32, '2024-2025-1', '选修', 40, '网页设计与制作基础', 6, '实验楼D401', 4, 5, 6),
('离散数学', 'MA201', 3.0, 48, '2024-2025-2', '选修', 50, '离散数学理论基础', 8, '教学楼A201', 5, 5, 6);

-- 选课数据
INSERT INTO student_course (student_id, course_id, select_time, status) VALUES
(1, 1, '2024-09-01 08:00:00', 1),
(1, 2, '2024-09-01 08:01:00', 1),
(1, 3, '2024-09-01 08:02:00', 1),
(1, 4, '2024-09-01 08:03:00', 1),
(1, 5, '2024-09-01 08:04:00', 1),
(1, 6, '2024-09-01 08:05:00', 1),
(1, 13, '2024-09-02 10:00:00', 1),
(2, 1, '2024-09-01 09:00:00', 1),
(2, 2, '2024-09-01 09:01:00', 1),
(2, 3, '2024-09-01 09:02:00', 1),
(2, 4, '2024-09-01 09:03:00', 1),
(2, 5, '2024-09-01 09:04:00', 1),
(3, 1, '2024-09-01 10:00:00', 1),
(3, 2, '2024-09-01 10:01:00', 1),
(3, 3, '2024-09-01 10:02:00', 1),
(3, 5, '2024-09-01 10:03:00', 1),
(3, 14, '2024-09-02 11:00:00', 1),
(4, 1, '2024-09-01 11:00:00', 1),
(4, 2, '2024-09-01 11:01:00', 1),
(4, 4, '2024-09-01 11:02:00', 1),
(4, 6, '2024-09-01 11:03:00', 1),
(5, 1, '2024-09-01 12:00:00', 1),
(5, 2, '2024-09-01 12:01:00', 1),
(5, 3, '2024-09-01 12:02:00', 1),
(6, 3, '2024-09-01 13:00:00', 1),
(6, 4, '2024-09-01 13:01:00', 1),
(6, 6, '2024-09-01 13:02:00', 1),
(6, 13, '2024-09-02 14:00:00', 1),
(7, 1, '2024-09-01 14:00:00', 1),
(7, 2, '2024-09-01 14:01:00', 1),
(7, 3, '2024-09-01 14:02:00', 1),
(8, 4, '2024-09-01 15:00:00', 1),
(8, 6, '2024-09-01 15:01:00', 1),
(8, 13, '2024-09-02 16:00:00', 1),
(9, 1, '2024-09-01 16:00:00', 1),
(9, 2, '2024-09-01 16:01:00', 1),
(9, 5, '2024-09-01 16:02:00', 1),
(10, 3, '2024-09-01 17:00:00', 1),
(10, 4, '2024-09-01 17:01:00', 1),
(10, 14, '2024-09-02 17:00:00', 1),
(11, 2, '2024-09-01 18:00:00', 1),
(11, 14, '2024-09-02 18:00:00', 1),
(12, 3, '2024-09-01 19:00:00', 1),
(12, 4, '2024-09-01 19:01:00', 1);

-- 成绩数据
INSERT INTO score (student_id, course_id, usual_score, final_score, total_score, gpa, is_pass) VALUES
(1, 1, 85.0, 90.0, 87.5, 3.8, 1),
(1, 2, 80.0, 75.0, 77.5, 2.8, 1),
(1, 3, 90.0, 85.0, 87.5, 3.8, 1),
(1, 4, 75.0, 80.0, 77.5, 2.8, 1),
(1, 5, 88.0, 92.0, 90.0, 4.0, 1),
(1, 13, 82.0, 78.0, 80.0, 3.0, 1),
(2, 1, 78.0, 82.0, 80.0, 3.0, 1),
(2, 2, 85.0, 88.0, 86.5, 3.7, 1),
(2, 3, 92.0, 95.0, 93.5, 4.0, 1),
(2, 4, 70.0, 65.0, 67.5, 1.8, 1),
(2, 5, 80.0, 85.0, 82.5, 3.3, 1),
(3, 1, 90.0, 88.0, 89.0, 3.9, 1),
(3, 2, 75.0, 70.0, 72.5, 2.3, 1),
(3, 3, 85.0, 80.0, 82.5, 3.3, 1),
(3, 5, 78.0, 82.0, 80.0, 3.0, 1),
(3, 14, 88.0, 90.0, 89.0, 3.9, 1),
(4, 1, 65.0, 58.0, 61.5, 1.2, 1),
(4, 2, 72.0, 75.0, 73.5, 2.4, 1),
(4, 4, 80.0, 85.0, 82.5, 3.3, 1),
(5, 1, 88.0, 92.0, 90.0, 4.0, 1),
(5, 2, 90.0, 85.0, 87.5, 3.8, 1),
(5, 3, 78.0, 82.0, 80.0, 3.0, 1),
(7, 1, 82.0, 78.0, 80.0, 3.0, 1),
(7, 2, 76.0, 80.0, 78.0, 2.8, 1),
(7, 3, 85.0, 88.0, 86.5, 3.7, 1),
(9, 1, 70.0, 75.0, 72.5, 2.3, 1),
(9, 2, 80.0, 82.0, 81.0, 3.1, 1),
(9, 5, 68.0, 72.0, 70.0, 2.0, 1);
