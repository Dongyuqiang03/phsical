alter table exam_record
    modify status tinyint default 0 not null comment '状态(0:待体检 1-进行中(未录入结果) 2-进行中(已录入结果) 3-已完成';
alter table exam_record
    add main_findings TEXT null comment '主要发现';

alter table exam_result
    change suggestion analysis text null comment '异常分析';

-- 添加用户编号字段
ALTER TABLE sys_user
    ADD COLUMN user_code VARCHAR(50) NOT NULL COMMENT '用户编号(学号/工号/医护编号)' AFTER username,
    ADD KEY uk_user_code (user_code);

-- 修改部门类型字段的注释
ALTER TABLE sys_department 
MODIFY COLUMN dept_type TINYINT NOT NULL COMMENT '部门类型(1:医疗科室 2:行政部门 3:教学院系 4:后勤部门 5:其他部门)';