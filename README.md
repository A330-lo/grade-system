# 学生选课成绩管理系统

基于 Spring Boot 4.0.0 + Vue 3 的学生选课成绩管理系统，Docker Compose 一键部署。

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 4.0.0 |
| Java | 21 |
| MyBatis Plus | 3.5.5 |
| MySQL | 8.0 |
| Redis | 7-Alpine |
| JWT | 0.12.3 (jjwt) |
| 前端 | Vue 3 (Vite) |

## 功能模块

- 学生管理 / 教师管理 / 管理员管理
- 院系管理 / 专业管理 / 班级管理
- 课程管理 / 选课管理 / 成绩管理
- JWT 登录认证与权限拦截（admin / teacher / student 三种角色）
- Redis 缓存

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | BCrypt 加密 |
| 教师 | T001 ~ T008 | 123456 | 8 位教师 |
| 学生 | 2024001 ~ 2024012 | 123456 | 12 名学生 |

## Docker Compose 部署

### 环境要求

- Docker & Docker Compose
- 端口占用: 8080（后端）、5713（前端）、3307（MySQL）、6380（Redis）

### 启动

```bash
docker compose up -d
```

### 访问

- 前端: http://localhost:5713
- 后端 API: http://localhost:8080/api

### 服务说明

| 服务 | 容器名 | 端口映射 |
|------|--------|----------|
| 后端 | grade-backend | 8080:8080 |
| 前端 (Nginx) | grade-frontend | 5713:80 |
| MySQL | grade-mysql | 3307:3306 |
| Redis | grade-redis | 6380:6379 |

### 重启单个服务

```bash
docker compose restart backend    # 重启后端
docker compose restart frontend   # 重启前端
```

### 查看日志

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

## 本地开发

### 环境要求

- JDK 21+
- MySQL 8.0+
- Redis
- Maven 3.9+

### 数据库

执行 `init.sql` 初始化数据库（含完整测试数据）。

### 配置

修改 `back/src/main/resources/application.yml` 中的数据库和 Redis 连接信息为 `localhost`。

### 运行后端

```bash
cd back
mvn spring-boot:run
```

### 运行前端

```bash
cd front
npm install
npm run dev
```

## 项目结构

```
grade/
├── back/                 # Spring Boot 后端
│   ├── src/main/java/    # Java 源码
│   ├── src/main/resources/ # 配置与 SQL 映射
│   └── Dockerfile        # 后端 Docker 镜像
├── front/                # Vue 3 前端
│   ├── src/              # 前端源码
│   └── Dockerfile        # 前端 Docker 镜像
├── docker-compose.yml    # Docker Compose 编排
├── init.sql              # 数据库初始化脚本
└── README.md
```

