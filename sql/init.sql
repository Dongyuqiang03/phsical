-- 创建数据库
CREATE DATABASE IF NOT EXISTS shpes DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE shpes;

-- 系统管理相关表
-- 用户表
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(50) COMMENT '邮箱',
    gender TINYINT COMMENT '性别(0:女 1:男)',
    avatar VARCHAR(255) COMMENT '头像URL',
    dept_id BIGINT COMMENT '部门ID',
    user_type TINYINT NOT NULL COMMENT '用户类型(1:医护人员 2:教职工 3:学生)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 部门表
CREATE TABLE sys_department (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    dept_code VARCHAR(50) NOT NULL COMMENT '部门编码',
    parent_id BIGINT COMMENT '父部门ID',
    description VARCHAR(255) COMMENT '部门描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';


-- 操作日志表
CREATE TABLE sys_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    method VARCHAR(255) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    status TINYINT COMMENT '操作状态(0:失败 1:成功)',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 体检业务相关表
-- 体检项目表
CREATE TABLE exam_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL COMMENT '项目编码',
    category_id BIGINT NOT NULL COMMENT '项目分类ID',
    dept_id BIGINT NOT NULL COMMENT '执行科室ID',
    reference_low VARCHAR(50) COMMENT '参考值下限',
    reference_high VARCHAR(50) COMMENT '参考值上限',
    unit VARCHAR(20) COMMENT '单位',
    description TEXT COMMENT '项目描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_code (item_code),
    KEY idx_category_id (category_id),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检项目表';

-- 体检套餐表
CREATE TABLE exam_package (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    package_name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    package_code VARCHAR(50) NOT NULL COMMENT '套餐编码',
    price DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    description TEXT COMMENT '套餐描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_package_code (package_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检套餐表';

-- 套餐项目关联表
CREATE TABLE exam_package_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    item_id BIGINT NOT NULL COMMENT '项目ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_package_item (package_id, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐项目关联表';

-- 时间段表
CREATE TABLE exam_time_slot (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
    dept_id BIGINT NOT NULL COMMENT '科室ID',
    exam_date DATE NOT NULL COMMENT '体检日期',
    time_period VARCHAR(20) NOT NULL COMMENT '时间段(上午/下午)',
    capacity INT NOT NULL COMMENT '容量',
    booked INT NOT NULL DEFAULT 0 COMMENT '已预约数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:停诊 1:正常)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_date_period (dept_id, exam_date, time_period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 体检预约表
CREATE TABLE exam_appointment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    time_slot_id BIGINT NOT NULL COMMENT '时间段ID',
    appointment_no VARCHAR(50) NOT NULL COMMENT '预约编号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:已取消 1:待体检 2:已完成)',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_appointment_no (appointment_no),
    KEY idx_user_id (user_id),
    KEY idx_time_slot_id (time_slot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检预约表';

-- 体检记录表
CREATE TABLE exam_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    appointment_id BIGINT NOT NULL COMMENT '预约ID',
    exam_no VARCHAR(50) NOT NULL COMMENT '体检编号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:进行中 2:已完成)',
    conclusion TEXT COMMENT '体检总结',
    suggestion TEXT COMMENT '医生建议',
    doctor_id BIGINT COMMENT '总检医生ID',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam_no (exam_no),
    KEY idx_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检记录表';

-- 体检结果表
CREATE TABLE exam_result (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '结果ID',
    record_id BIGINT NOT NULL COMMENT '体检记录ID',
    item_id BIGINT NOT NULL COMMENT '体检项目ID',
    result_value VARCHAR(255) NOT NULL COMMENT '检查结果',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常(0:正常 1:异常)',
    doctor_id BIGINT NOT NULL COMMENT '检查医生ID',
    review_doctor_id BIGINT COMMENT '复核医生ID',
    review_time DATETIME COMMENT '复核时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_item (record_id, item_id),
    KEY idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检结果表';

-- 通知记录表
CREATE TABLE notice_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    notice_type TINYINT NOT NULL COMMENT '通知类型(1:系统通知 2:预约提醒 3:结果通知)',
    read_status TINYINT NOT NULL DEFAULT 0 COMMENT '读取状态(0:未读 1:已读)',
    read_time DATETIME COMMENT '读取时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_receiver_id (receiver_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';


-- ... 原有建表语句保持不变 ...

-- 初始化系统角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('医生', 'ROLE_DOCTOR', '体检医生，负责体检结果录入和审核'),
('护士', 'ROLE_NURSE', '护士，负责体检预约和基础信息录入'),
('教职工', 'ROLE_STAFF', '教职工用户'),
('学生', 'ROLE_STUDENT', '学生用户');

-- 初始化部门数据
INSERT INTO sys_department (dept_name, dept_code, parent_id, description) VALUES
(NULL, '校医院总院', 'HOSPITAL', NULL, '校医院总部'),
('内科', 'INTERNAL', 1, '内科检查'),
('外科', 'SURGERY', 1, '外科检查'),
('化验科', 'LABORATORY', 1, '血液、尿液等化验'),
('影像科', 'RADIOLOGY', 1, 'X光、B超等检查'),
('体检中心', 'PHYSICAL', 1, '体检管理部门');

-- 初始化系统管理员账号(123456)
INSERT INTO sys_user (username, password, real_name, user_type, dept_id) VALUES
('admin', '$2a$10$VQIknwW5PUZ6SQQyBF1YVOgtJXQqGbwFxwEQxQPLBzLXrWlGCGcOy', '系统管理员', 1, 1);

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化字典数据
-- 性别字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort) VALUES
('gender', '男', '1', 1),
('gender', '女', '0', 2);

-- 用户类型字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort) VALUES
('user_type', '医护人员', '1', 1),
('user_type', '教职工', '2', 2),
('user_type', '学生', '3', 3);

-- 体检项目分类字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort) VALUES
('exam_category', '一般检查', '1', 1),
('exam_category', '内科', '2', 2),
('exam_category', '外科', '3', 3),
('exam_category', '血常规', '4', 4),
('exam_category', '尿常规', '5', 5),
('exam_category', '肝功能', '6', 6),
('exam_category', 'B超', '7', 7),
('exam_category', 'X光', '8', 8);

-- 初始化基础体检项目
INSERT INTO exam_item (item_name, item_code, category_id, dept_id, reference_low, reference_high, unit) VALUES
('身高', 'HEIGHT', 1, 5, '140', '200', 'cm'),
('体重', 'WEIGHT', 1, 5, '40', '100', 'kg'),
('血压', 'BP', 1, 5, '90/60', '140/90', 'mmHg'),
('血常规', 'BLOOD', 4, 3, NULL, NULL, NULL),
('尿常规', 'URINE', 5, 3, NULL, NULL, NULL),
('胸部X光', 'CHEST_X', 8, 4, NULL, NULL, NULL),
('腹部B超', 'ABDOMEN_B', 7, 4, NULL, NULL, NULL);

-- 初始化基础体检套餐
INSERT INTO exam_package (package_name, package_code, price, description) VALUES
('入学体检套餐', 'PKG_ENTRANCE', 150.00, '适用于新生入学体检'),
('教职工体检套餐', 'PKG_STAFF', 300.00, '适用于教职工年度体检');

-- 初始化套餐项目关联
INSERT INTO exam_package_item (package_id, item_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), -- 入学体检套餐项目
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7); -- 教职工体检套餐项目