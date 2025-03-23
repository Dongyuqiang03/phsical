-- 初始化系统权限
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, component, icon, sort) VALUES
-- 系统管理
('系统管理', 'system', 1, NULL, '/system', 'Layout', 'system', 1),
('用户管理', 'system:user', 1, 1, 'user', 'system/user/index', 'user', 1),
('角色管理', 'system:role', 1, 1, 'role', 'system/role/index', 'role', 2),
('权限管理', 'system:permission', 1, 1, 'permission', 'system/permission/index', 'permission', 3),
('部门管理', 'system:department', 1, 1, 'department', 'system/department/index', 'department', 4),
('日志管理', 'system:log', 1, 1, 'log', 'system/log/index', 'log', 5),

-- 体检管理
('体检管理', 'exam', 1, NULL, '/exam', 'Layout', 'exam', 2),
('体检项目', 'exam:item', 1, 8, 'item', 'exam/item/index', 'item', 1),
('体检套餐', 'exam:package', 1, 8, 'package', 'exam/package/index', 'package', 2),
('预约时间段', 'exam:timeslot', 1, 8, 'timeslot', 'exam/timeslot/index', 'time', 3),
('预约管理', 'exam:appointment', 1, 8, 'appointment', 'exam/appointment/index', 'appointment', 4),
('体检记录', 'exam:record', 1, 8, 'record', 'exam/record/index', 'record', 5),
('体检结果', 'exam:result', 1, 8, 'result', 'exam/result/index', 'result', 6),
('体检项目分类', 'exam:category', 1, 8, 'category', 'exam/category/index', 'category', 7);

-- 为系统管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 为医护人员角色分配体检相关权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code LIKE 'exam%';

-- 为普通用户分配必要的菜单权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE permission_code IN ('exam:item', 'exam:category');

-- 普通用户不需要菜单权限，通过角色控制接口访问