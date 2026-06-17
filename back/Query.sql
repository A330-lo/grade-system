-- 更新教师名字为真实姓名（根据工号更新，更准确）
UPDATE teacher SET teacher_name = '张伟' WHERE teacher_no = 'T001';
UPDATE teacher SET teacher_name = '李娜' WHERE teacher_no = 'T002';
UPDATE teacher SET teacher_name = '王芳' WHERE teacher_no = 'T003';
UPDATE teacher SET teacher_name = '刘洋' WHERE teacher_no = 'T004';
UPDATE teacher SET teacher_name = '陈静' WHERE teacher_no = 'T005';
UPDATE teacher SET teacher_name = '赵强' WHERE teacher_no = 'T006';
UPDATE teacher SET teacher_name = '孙磊' WHERE teacher_no = 'T007';
UPDATE teacher SET teacher_name = '周丽' WHERE teacher_no = 'T008';
-- 更新部门名称和编码（将"系"改为"学院"）
UPDATE department SET dept_name = '计算机科学与技术学院', dept_code = 'CS' WHERE id = 1;
UPDATE department SET dept_name = '软件工程学院', dept_code = 'SE' WHERE id = 2;
UPDATE department SET dept_name = '信息管理学院', dept_code = 'IM' WHERE id = 3;
UPDATE department SET dept_name = '电子工程学院', dept_code = 'EE' WHERE id = 4;
UPDATE department SET dept_name = '数学学院', dept_code = 'MA' WHERE id = 5;
-- 新增学院信息
INSERT INTO department (id, dept_name, dept_code) VALUES (6, '外国语学院', 'FL');
INSERT INTO department (id, dept_name, dept_code) VALUES (7, '机械工程学院', 'ME');
INSERT INTO department (id, dept_name, dept_code) VALUES (8, '土木工程学院', 'CE');
INSERT INTO department (id, dept_name, dept_code) VALUES (9, '艺术设计学院', 'AD');
INSERT INTO department (id, dept_name, dept_code) VALUES (10, '经济管理学院', 'EM');
INSERT INTO department (id, dept_name, dept_code) VALUES (11, '物理学院', 'PHY');
INSERT INTO department (id, dept_name, dept_code) VALUES (12, '化学学院', 'CHEM');
INSERT INTO department (id, dept_name, dept_code) VALUES (13, '生物工程学院', 'BE');
INSERT INTO department (id, dept_name, dept_code) VALUES (14, '新闻传播学院', 'JC');
INSERT INTO department (id, dept_name, dept_code) VALUES (15, '法学院', 'LAW');
ALTER TABLE score ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除';
ALTER TABLE score ADD COLUMN status TINYINT DEFAULT 0 COMMENT '状态：0-未发布(草稿)，1-已发布';
