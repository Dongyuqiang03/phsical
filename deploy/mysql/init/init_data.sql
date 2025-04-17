-- 初始化系统管理员账号(密码: 123456)
INSERT INTO sys_user (username, password, real_name, user_type, dept_id)
VALUES ('admin', '31629dcfd1f33ac5d66f29a20ca06aac9d4434e5108b41b2241fe1cac5695073', '系统管理员', 1, 1);

-- 初始化系统角色
INSERT INTO sys_role (role_name, role_code, description)
VALUES ('管理员', 'ROLE_ADMIN', '系统管理员，可以进行系统管理'),
       ('医护人员', 'ROLE_MEDICAL', '医护人员，负责体检相关工作'),
       ('普通用户', 'ROLE_USER', '普通用户，可以预约体检和查看自己的体检记录');

-- 初始化部门数据
INSERT INTO sys_department (dept_name, dept_code, parent_id, dept_type, description) VALUES
-- 医疗科室
('内科', 'INTERNAL', NULL, 1, '内科系统检查'),
('心内科', 'CARDIOLOGY', 1, 1, '心脏相关检查'),
('消化内科', 'GASTROENTEROLOGY', 1, 1, '消化系统检查'),
('呼吸内科', 'RESPIRATORY', 1, 1, '呼吸系统检查'),
('内分泌科', 'ENDOCRINOLOGY', 1, 1, '内分泌系统检查'),
('外科', 'SURGERY', NULL, 1, '外科系统检查'),
('普外科', 'GENERAL_SURGERY', 6, 1, '普通外科检查'),
('骨科', 'ORTHOPEDICS', 6, 1, '骨科检查'),
('检验科', 'LABORATORY', NULL, 1, '各类检验检查'),
('影像科', 'RADIOLOGY', NULL, 1, '影像学检查'),
('超声科', 'ULTRASOUND', NULL, 1, '超声检查'),
('心电图室', 'ECG', NULL, 1, '心电图检查'),
('体检中心', 'PHYSICAL', NULL, 1, '体检管理'),
('营养科', 'NUTRITION', NULL, 1, '营养评估'),
('眼科', 'OPHTHALMOLOGY', NULL, 1, '眼科检查'),
('耳鼻喉科', 'ENT', NULL, 1, '耳鼻喉检查'),
('口腔科', 'DENTAL', NULL, 1, '口腔检查'),
-- 其他部门
('系统管理', 'SYSTEM', NULL, 2, '系统管理部门');


-- 初始化用户角色关联（系统管理员同时具有管理员和医护人员角色）
INSERT INTO sys_user_role (user_id, role_id)
SELECT 1, id
FROM sys_role
WHERE role_code IN ('ROLE_ADMIN', 'ROLE_MEDICAL');

-- 初始化体检项目分类数据
INSERT INTO exam_item_category (name, code, status)
VALUES ('常规检查', 'ROUTINE', 1),
       ('实验室检查', 'LAB', 1),
       ('医学影像', 'IMAGING', 1),
       ('其他检查', 'OTHER', 1);

-- 初始化体检项目数据
INSERT INTO exam_item (name, code, category_id, dept_id, reference_value, price, status) VALUES
-- 内科系统检查项目
('血压', 'BP', 1, 1, '90/60-140/90mmHg', 1500, 1),
('心率', 'HR', 1, 1, '60-100次/分', 1000, 1),
('呼吸频率', 'RR', 1, 1, '12-20次/分', 1000, 1),
('体温', 'TEMP', 1, 1, '36.3-37.2℃', 1000, 1),
('血常规', 'BLOOD', 2, 9, '红细胞(RBC): 4.0-5.5×10^12/L; 血红蛋白(HGB): 120-160g/L; 白细胞(WBC): 4.0-10.0×10^9/L; 血小板(PLT): 100-300×10^9/L', 3000, 1),
('尿常规', 'URINE', 2, 9, '颜色: 淡黄色或黄色透明; 尿蛋白: 阴性; 尿糖: 阴性; 尿胆原: 阴性; pH值: 5.0-8.0; 尿比重: 1.003-1.030', 3000, 1),
('肝功能', 'LIVER', 2, 9, '谷丙转氨酶(ALT): 0-40U/L; 谷草转氨酶(AST): 0-40U/L; 总胆红素(TBIL): 5.1-17.1μmol/L; 直接胆红素(DBIL): 0-6.8μmol/L', 4000, 1),
('肾功能', 'KIDNEY', 2, 9, '尿素氮(BUN): 2.86-7.14mmol/L; 肌酐(Cr): 44-133μmol/L; 尿酸(UA): 149-416μmol/L', 4000, 1),
('血脂', 'LIPID', 2, 9, '总胆固醇(TC): 3.1-5.7mmol/L; 甘油三酯(TG): 0.4-1.7mmol/L; 高密度脂蛋白(HDL-C): 0.9-1.8mmol/L; 低密度脂蛋白(LDL-C): 1.9-3.1mmol/L', 3500, 1),
('血糖', 'GLU', 2, 9, '空腹血糖: 3.9-6.1mmol/L; 餐后2小时血糖: <7.8mmol/L', 2000, 1),
('甲状腺功能', 'THYROID', 2, 9, 'TSH: 0.27-4.2mIU/L; FT3: 3.1-6.8pmol/L; FT4: 12-22pmol/L', 5000, 1),
('胸部X光', 'CHEST_X', 3, 10, '肺野清晰，心影大小、形态正常，纵隔宽度正常，膈肌光滑规则', 5000, 1),
('腹部B超', 'ABDOMEN_B', 3, 11, '肝脏: 大小正常，形态正常，回声均匀; 胆囊: 大小正常，囊壁光滑，腔内未见明显异常回声; 脾脏: 大小正常，回声均匀; 胰腺: 显示不清，未见明显异常回声; 肾脏: 双肾大小正常，形态正常，皮质厚度正常，集合系统未见明显扩张', 8000, 1),
('心电图', 'ECG', 3, 12, '心率: 60-100次/分; 心律: 窦性心律; 各间期正常; 无明显ST-T改变', 3000, 1),
('骨密度', 'BONE', 3, 10, 'T值≥-1.0为正常; -2.5<T值<-1.0为骨量减少; T值≤-2.5为骨质疏松', 5000, 1),
('身高', 'HEIGHT', 1, 13, '140-200cm', 1000, 1),
('体重', 'WEIGHT', 1, 13, '40-100kg', 1000, 1),
('腰围', 'WAIST', 1, 13, '男性<90cm; 女性<85cm', 1000, 1),
('体脂率', 'BODY_FAT', 1, 13, '男性15-25%; 女性20-30%', 2000, 1),
('营养评估', 'NUTRITION', 1, 14, 'BMI: 18.5-24.0; 营养状况评分', 2000, 1),
('视力', 'VISION', 1, 15, '裸眼视力≥1.0; 矫正视力≥1.0', 2000, 1),
('眼底检查', 'FUNDUS', 1, 15, '视乳头边界清，视网膜血管走行正常，黄斑区未见明显异常', 3000, 1),
('听力', 'HEARING', 1, 16, '纯音测听: 各频率听阈≤25dB', 3000, 1),
('口腔检查', 'DENTAL', 1, 17, '牙齿排列整齐，牙龈无红肿，口腔黏膜完整', 2000, 1);

-- 初始化基础体检套餐
INSERT INTO exam_package (name, code, price, original_price, gender, status)
VALUES ('入学体检套餐', 'PKG_ENTRANCE', 15000, 18000, 0, 1),
       ('教职工体检套餐', 'PKG_STAFF', 30000, 35000, 0, 1);

-- 初始化套餐项目关联
INSERT INTO exam_package_item (package_id, item_id)
VALUES
-- 入学体检套餐项目（基础检查项目）
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 9),
-- 教职工体检套餐项目（全面检查项目）
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 6),
(2, 7),
(2, 8),
(2, 9),
(2, 10),
(2, 11),
(2, 12);

-- 初始化系统权限
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, component, icon) VALUES
-- 系统管理
('系统管理', 'system', 1, NULL, '/system', 'Layout', 'system'),
('用户管理', 'system:user', 1, 1, 'user', 'system/user/index', 'user'),
('角色管理', 'system:role', 1, 1, 'role', 'system/role/index', 'role'),
('权限管理', 'system:permission', 1, 1, 'permission', 'system/permission/index', 'permission'),
('部门管理', 'system:department', 1, 1, 'department', 'system/department/index', 'department'),
('日志管理', 'system:log', 1, 1, 'log', 'system/log/index', 'log'),

-- 体检管理
('体检管理', 'exam', 1, NULL, '/exam', 'Layout', 'exam'),
('体检项目', 'exam:item', 1, 8, 'item', 'exam/item/index', 'item'),
('体检套餐', 'exam:package', 1, 8, 'package', 'exam/package/index', 'package'),
('预约时间段', 'exam:timeslot', 1, 8, 'timeslot', 'exam/timeslot/index', 'time'),
('预约管理', 'exam:appointment', 1, 8, 'appointment', 'exam/appointment/index', 'appointment'),
('体检记录', 'exam:record', 1, 8, 'record', 'exam/record/index', 'record'),
('体检结果', 'exam:result', 1, 8, 'result', 'exam/result/index', 'result'),
('体检项目分类', 'exam:category', 1, 8, 'category', 'exam/category/index', 'category');

-- 为系统管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 为医护人员角色分配体检相关权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code LIKE 'exam%';

-- 为普通用户分配必要的菜单权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE permission_code IN ('exam:item', 'exam:category','exam:appointment');

alter table exam_record
    modify status tinyint default 0 not null comment '状态(0:待体检 1-进行中(未录入结果) 2-进行中(已录入结果) 3-已完成';
alter table exam_record
    add main_findings TEXT null comment '主要发现';

alter table exam_result
    change suggestion analysis text null comment '异常分析';
