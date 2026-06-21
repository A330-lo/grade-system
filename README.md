# grade-system
# 学生选课成绩管理系统

基于 Spring Boot 3 的学生选课成绩管理系统后端。

## 技术栈

- **Spring Boot** 3.4.2
- **MyBatis Plus** 3.5.5
- **MySQL** + **Redis**
- **JWT** 认证
- **Java** 17

## 功能模块

- 学生管理 / 教师管理 / 管理员管理
- 院系管理 / 专业管理 / 班级管理
- 课程管理 / 选课管理 / 成绩管理
- JWT 登录认证与权限拦截

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis
- Maven 3.6+

### 数据库

执行 `Query.sql` 初始化数据库。

### 配置

修改 `src/main/resources/application.yml` 中的数据库和 Redis 连接信息。

### 运行

```bash
mvn spring-boot:run
```

启动后访问: `http://localhost:8080/api`

### Docker 部署

```bash
docker build -t student-course-system .
docker run -p 8080:8080 student-course-system
```
