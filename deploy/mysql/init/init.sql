-- Create database
-- 创建数据库
CREATE DATABASE IF NOT EXISTS shpes DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shpes;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

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

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL COMMENT '权限编码',
    permission_type TINYINT NOT NULL COMMENT '权限类型(1:菜单 2:按钮)',
    parent_id BIGINT COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '路径',
    component VARCHAR(100) COMMENT '组件',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

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
-- 体检项目分类表
CREATE TABLE exam_item_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL COMMENT '分类编码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检项目分类表';

-- 体检项目表
CREATE TABLE exam_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    code VARCHAR(50) NOT NULL COMMENT '项目编码',
    category_id BIGINT NOT NULL COMMENT '项目分类ID',
    department_id BIGINT NOT NULL COMMENT '执行科室ID',
    lower_limit VARCHAR(50) COMMENT '参考值下限',
    upper_limit VARCHAR(50) COMMENT '参考值上限',
    reference_value VARCHAR(255) COMMENT '参考值描述',
    unit VARCHAR(20) COMMENT '单位',
    price INT COMMENT '价格（分）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_code (code),
    KEY idx_category_id (category_id),
    KEY idx_dept_id (department_id),
    CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES exam_item_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检项目表';

-- 体检套餐表
CREATE TABLE exam_package (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    code VARCHAR(50) NOT NULL COMMENT '套餐编码',
    price INT NOT NULL COMMENT '套餐价格（分）',
    original_price INT COMMENT '原价（分）',
    gender INT DEFAULT 0 COMMENT '适用性别：0-不限，1-男，2-女',
    sort INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(255) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_package_code (code)
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
    department_id BIGINT NOT NULL COMMENT '科室ID',
    date DATE NOT NULL COMMENT '体检日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    capacity INT NOT NULL COMMENT '容量',
    booked_count INT NOT NULL DEFAULT 0 COMMENT '已预约数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_dept_date (department_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 体检预约表
CREATE TABLE exam_appointment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    appointment_no VARCHAR(50) NOT NULL COMMENT '预约编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    package_name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    time_slot_id BIGINT NOT NULL COMMENT '时间段ID',
    department_id BIGINT NOT NULL COMMENT '部门ID',
    department_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:待确认 1:待体检 2:进行中 3:已完成 4:已取消)',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    cancel_time DATETIME COMMENT '取消时间',
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
    exam_no VARCHAR(50) NOT NULL COMMENT '体检编号',
    appointment_id BIGINT NOT NULL COMMENT '预约ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    package_name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    department_id BIGINT NOT NULL COMMENT '部门ID',
    department_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    exam_date DATETIME NOT NULL COMMENT '体检日期',
    conclusion TEXT COMMENT '体检结论',
    suggestion TEXT COMMENT '医生建议',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0:待体检 1:进行中 2:已完成)',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam_no (exam_no),
    KEY idx_appointment_id (appointment_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检记录表';

-- 体检结果表
CREATE TABLE exam_result (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '结果ID',
    record_id BIGINT NOT NULL COMMENT '体检记录ID',
    item_id BIGINT NOT NULL COMMENT '体检项目ID',
    value VARCHAR(255) NOT NULL COMMENT '检查结果值',
    unit VARCHAR(20) COMMENT '结果单位',
    abnormal TINYINT DEFAULT 0 COMMENT '是否异常(0:正常 1:异常)',
    suggestion TEXT COMMENT '医生建议',
    doctor_id BIGINT NOT NULL COMMENT '检查医生ID',
    reviewer_id BIGINT COMMENT '复核医生ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:待复核 2:已复核)',
    remark VARCHAR(255) COMMENT '备注',
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

-- 初始化系统角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('管理员', 'ROLE_ADMIN', '系统管理员，可以进行系统管理'),
('医护人员', 'ROLE_MEDICAL', '医护人员，负责体检相关工作'),
('普通用户', 'ROLE_USER', '普通用户，可以预约体检和查看自己的体检记录');

-- 初始化部门数据
INSERT INTO sys_department (dept_name, dept_code, parent_id, description) VALUES
('校医院总院', 'HOSPITAL', NULL, '校医院总部'),
('内科', 'INTERNAL', 1, '内科检查'),
('外科', 'SURGERY', 1, '外科检查'),
('化验科', 'LABORATORY', 1, '血液、尿液等化验'),
('影像科', 'RADIOLOGY', 1, 'X光、B超等检查'),
('体检中心', 'PHYSICAL', 1, '体检管理部门'),
('教学部门', 'TEACHING', NULL, '教学单位总部'),
('计算机学院', 'CS', 7, '计算机科学与技术学院'),
('医学院', 'MED', 7, '医学院');

-- 初始化系统管理员账号(密码: 123456)
INSERT INTO sys_user (username, password, real_name, user_type, dept_id) VALUES
('admin', '$2a$10$VQIknwW5PUZ6SQQyBF1YVOgtJXQqGbwFxwEQxQPLBzLXrWlGCGcOy', '系统管理员', 1, 1);

-- 初始化用户角色关联（系统管理员同时具有管理员和医护人员角色）
INSERT INTO sys_user_role (user_id, role_id) 
SELECT 1, id FROM sys_role WHERE role_code IN ('ROLE_ADMIN', 'ROLE_MEDICAL');

-- 初始化体检项目分类数据
INSERT INTO exam_item_category (name, code, status) VALUES
('常规检查', 'ROUTINE', 1),
('实验室检查', 'LAB', 1),
('医学影像', 'IMAGING', 1),
('其他检查', 'OTHER', 1);

-- 初始化基础体检项目
INSERT INTO exam_item (name, code, category_id, department_id, lower_limit, upper_limit, unit, price, status) VALUES
('身高', 'HEIGHT', 1, 5, '140', '200', 'cm', 1000, 1),
('体重', 'WEIGHT', 1, 5, '40', '100', 'kg', 1000, 1),
('血压', 'BP', 1, 5, '90/60', '140/90', 'mmHg', 1500, 1),
('血常规', 'BLOOD', 2, 3, NULL, NULL, NULL, 3000, 1),
('尿常规', 'URINE', 2, 3, NULL, NULL, NULL, 3000, 1),
('胸部X光', 'CHEST_X', 3, 4, NULL, NULL, NULL, 5000, 1),
('腹部B超', 'ABDOMEN_B', 3, 4, NULL, NULL, NULL, 8000, 1);

-- 初始化基础体检套餐
INSERT INTO exam_package (name, code, price, original_price, gender, sort, status) VALUES
('入学体检套餐', 'PKG_ENTRANCE', 15000, 18000, 0, 1, 1),
('教职工体检套餐', 'PKG_STAFF', 30000, 35000, 0, 2, 1);

-- 初始化套餐项目关联
INSERT INTO exam_package_item (package_id, item_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), -- 入学体检套餐项目
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7); -- 教职工体检套餐项目