# 校医院体检信息系统技术方案

## 项目背景与目标
  本系统旨在实现校医院体检信息的智能化管理，提升体检流程的效率与服务质量。系统支持学校教职工和学生进行信息管理、体检预约、体检安排，以及体检结果的记录和查询。

## 一、技术选型

### 1. 前端技术栈
- Vue 2.x
- Vue Router 3.x
- Vuex 3.x
- Element UI 2.15.x
- Axios 0.21.x
- ECharts 5.x (数据可视化)

### 2. 后端技术栈
- SpringBoot 2.7.x
- JDK 1.8
- MySQL 8.0
- MyBatis-Plus 3.5.x
- Hutool 5.8.x
- Lombok
- Knife4j 3.x (API文档)
- JWT 0.9.x (认证授权)

## 二、系统架构

### 1. 整体架构
采用前后端分离架构，后端提供RESTful API接口，前端通过API调用获取数据。

### 2. 项目结构
```plaintext
phsical/
├── shpes-ui/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # Vuex状态管理
│   │   ├── utils/            # 工具类
│   │   └── views/            # 页面组件
│   └── package.json
│
└── shpes-server/               # 后端项目
    ├── src/main/java/
    │   └── com/shpes/
    │       ├── common/        # 公共模块
    │       ├── config/        # 配置类
    │       ├── controller/    # 控制器
    │       ├── entity/        # 实体类
    │       ├── mapper/        # MyBatis接口
    │       ├── service/       # 服务层
    │       └── utils/         # 工具类
    └── pom.xml
```

## 三、数据库设计
### 主要数据表
1. 系统管理相关表
   - 用户表(sys_user)：统一管理所有用户，包括医护人员、教职工和学生
   - 角色表(sys_role)：管理系统角色
   - 用户角色关联表(sys_user_role)：用户和角色多对多关联
   - 部门表(sys_department)：管理体检科室信息
   - 字典表(sys_dict)：系统通用字典管理
   - 操作日志表(sys_log)：记录重要操作日志

2. 体检业务相关表
   - 体检项目表(exam_item)：体检项目基础信息，包含参考值范围
   - 体检套餐表(exam_package)：预设的体检套餐
   - 套餐项目关联表(exam_package_item)：套餐和项目多对多关联
   - 时间段表(exam_time_slot)：体检时间段和科室容量设置
   - 体检预约表(exam_appointment)：用户体检预约记录
   - 体检记录表(exam_record)：体检实际记录，包含总体结论
   - 体检结果表(exam_result)：具体项目检查结果
   - 通知记录表(notice_record)：通知发送和接收记录
详细的数据库初始化脚本请参考：[init.sql](sql/init.sql)

主要特点：
1. 采用utf8mb4字符集，支持完整的Unicode字符集
2. 所有表都包含基础的审计字段（创建时间、更新时间）
3. 设置了合理的索引，包括：
   - 主键索引
   - 唯一索引（用户名、角色编码等）
   - 外键索引（关联字段）
4. 包含了必要的初始化数据：
   - 系统角色：管理员、医生、护士、教职工、学生
   - 基础部门：校医院各科室
   - 常用字典数据
   - 基础体检项目和套餐
   
### 核心表关系
1. 用户权限关系
   - sys_user -> sys_user_role -> sys_role：用户角色关联
   - sys_user -> sys_department：用户所属部门关系

2. 预约流程关系
   - sys_user -> exam_appointment：用户预约关系
   - exam_appointment -> exam_package：预约套餐关系
   - exam_package -> exam_package_item -> exam_item：套餐项目关系
   - exam_appointment -> exam_time_slot：预约时间关系
   - exam_time_slot -> sys_department：科室时间段关系

3. 体检结果关系
   - exam_appointment -> exam_record：预约到实际体检记录关系
   - exam_record -> exam_result：体检记录与具体结果关系
   - exam_result -> exam_item：结果对应检查项目及其参考值
   - exam_record：包含体检总体结论和建议

4. 通知关系
   - notice_record -> sys_user：通知与用户关系，包含发送和接收状态

## 四、核心功能模块

### 第一阶段（必要功能）
### 1. 用户认证与权限管理
- 账号登录/登出
- 密码重置与修改
- 基于角色的权限控制
  - 角色分配与管理
  - 用户-角色关联维护
- 部门管理
  - 科室信息维护
  - 人员配置管理

### 2. 体检项目管理
- 体检项目维护
  - 项目基本信息管理
  - 项目分类管理（基于字典表）
  - 项目参考值设置
  - 项目执行科室关联
- 体检套餐配置
  - 套餐信息管理
  - 套餐项目组合配置

### 3. 预约管理
- 时间段设置
  - 体检时间段配置
  - 科室容量设置
- 在线预约服务
  - 套餐选择
  - 时间段选择
  - 预约确认
- 预约变更处理
  - 预约时间修改
  - 预约取消

### 4. 体检管理
- 体检记录管理
  - 预约信息核验
  - 体检记录创建
  - 体检完成确认
- 结果管理
  - 分项目结果录入
  - 异常值标记
  - 结果复核
  - 体检总结撰写
- 结果查询服务
  - 个人结果查询
  - 结果导出打印

### 第二阶段（扩展功能）
### 5. 通知管理
- 通知发送管理
  - 通知创建与发送
  - 接收人群选择
  - 发送状态跟踪
- 通知查看管理
  - 通知状态更新
  - 已读确认

### 6. 统计分析
- 预约情况统计
  - 预约量统计
  - 科室预约负载
  - 取消率分析
- 体检情况统计
  - 完成率统计
  - 异常率统计
  - 科室工作量统计

### 7. 其他扩展功能
- 关键操作日志记录
- 套餐定价管理
- 可预约日期管理
- 爽约记录管理
- 体检进度跟踪
- 医生评价录入

## 五、接口设计
采用RESTful风格API，主要接口包括：

### 1. 认证接口 (/api/auth)
- POST /login              # 用户登录
- POST /logout             # 用户登出
- POST /password/reset     # 密码重置
- POST /password/change    # 修改密码
- GET  /captcha           # 获取验证码
- POST /token/refresh     # 刷新Token
- GET  /current           # 获取当前用户信息

### 2. 系统管理接口
#### 2.1 用户管理 (/api/users)
- GET  /                  # 获取用户列表，支持分页和筛选
- GET  /{id}             # 获取用户详情
- POST /                  # 创建用户
- PUT  /{id}             # 更新用户信息
- POST /status           # 启用/禁用用户

#### 2.2 角色管理 (/api/roles)
- GET  /                  # 获取角色列表
- POST /                  # 创建角色
- PUT  /{id}             # 更新角色
- POST /{roleId}/users    # 分配用户到角色

#### 2.3 部门管理 (/api/departments)
- GET  /                  # 获取部门列表
- POST /                  # 创建部门
- PUT  /{id}             # 更新部门信息
- GET  /{id}/users       # 获取部门人员列表

### 3. 体检项目接口 (/api/items)
- GET  /                  # 获取项目列表，支持分页和筛选
- POST /                  # 创建体检项目
- PUT  /{id}             # 更新项目信息
- GET  /categories       # 获取项目分类（字典）
- GET  /{id}/reference   # 获取项目参考值

### 4. 体检套餐接口 (/api/packages)
- GET  /                  # 获取套餐列表，支持分页和筛选
- POST /                  # 创建套餐
- PUT  /{id}             # 更新套餐信息
- POST /{id}/items       # 配置套餐项目
- GET  /{id}/items       # 获取套餐项目列表

### 5. 预约管理接口
#### 5.1 时间段管理 (/api/timeslots)
- GET  /                  # 获取时间段列表，支持按日期和科室筛选
- POST /                  # 创建时间段
- PUT  /{id}             # 更新时间段信息
- GET  /available        # 获取可预约时间段

#### 5.2 预约管理 (/api/appointments)
- GET  /                  # 获取预约列表，支持多条件筛选
- POST /                  # 创建预约
- PUT  /{id}             # 修改预约
- PUT  /{id}/cancel      # 取消预约
- GET  /user/{userId}    # 获取用户预约记录

### 6. 体检管理接口
#### 6.1 体检记录 (/api/records)
- GET  /                  # 获取体检记录列表
- POST /                  # 创建体检记录
- PUT  /{id}/status      # 更新体检状态
- PUT  /{id}/complete    # 完成体检

#### 6.2 体检结果 (/api/results)
- GET  /record/{recordId} # 获取体检记录的所有结果
- POST /                  # 录入体检结果（支持批量）
- PUT  /{id}             # 更新结果
- POST /{id}/review      # 结果复核
- GET  /export/{recordId} # 导出体检报告


### 7. 通知管理接口 (/api/notices) [二期功能]
- GET  /                  # 获取通知列表，支持分页和筛选
- POST /                  # 创建通知
- PUT  /{id}             # 更新通知
- POST /batch            # 批量发送通知
- GET  /user/{userId}    # 获取用户通知列表
- PUT  /{id}/read        # 标记通知为已读
- GET  /unread/count     # 获取未读通知数量

### 8. 统计分析接口 (/api/stats) [二期功能]
#### 8.1 预约统计
- GET  /appointments/summary           # 获取预约统计概况
- GET  /appointments/trend            # 获取预约趋势数据
- GET  /appointments/department       # 获取科室预约负载
- GET  /appointments/cancel-rate      # 获取预约取消率

#### 8.2 体检统计
- GET  /exams/completion-rate         # 获取体检完成率
- GET  /exams/abnormal-rate          # 获取异常率统计
- GET  /exams/department-workload    # 获取科室工作量
- GET  /exams/daily-summary         # 获取每日体检汇总

### 9. 日志管理接口 (/api/logs) [二期功能]
- GET  /                  # 获取操作日志列表
- GET  /user/{userId}     # 获取用户操作日志
- GET  /export            # 导出日志数据

## 六、安全性考虑
### 1. 认证安全
- JWT token认证
- 密码BCrypt加密
- 登录失败次数限制

### 2. 接口安全
- 验证码校验
- 请求参数验证
- 防XSS攻击
- SQL注入防护

### 3. 数据安全
- 数据权限控制
- 敏感信息加密
- 操作日志记录

## 七、部署方案
### 1. 开发环境
- 前端: Node.js环境
- 后端: JDK 1.8
- 数据库: MySQL 8.0

### 2. 生产环境
- 前端: Nginx部署静态资源
- 后端: SpringBoot内嵌Tomcat部署
- 数据库: MySQL单机部署

### 3. 基础监控
- 服务器基础监控
- 应用日志监控
- 数据库监控

## 八、前端设计规范
### 1. 状态管理
- Vuex模块化设计
  - user模块：用户信息和认证状态
  - app模块：全局UI状态
  - exam模块：体检相关状态

### 2. UI/UX规范
- 设计系统
  - 统一色彩系统
  - 字体规范
  - 组件样式指南
- 响应式设计
  - 移动端适配
- 交互规范
  - 表单验证反馈
  - 加载状态展示
  - 错误处理展示

## 九、测试策略
### 1. 单元测试
- 后端测试框架：JUnit 5
- 核心业务逻辑测试用例

### 2. 集成测试
- API接口测试
- 数据库操作测试

## 十、API文档规范
### 1. 接口文档
- Knife4j (Swagger) 规范
- 错误码规范
- 响应格式规范

### 2. 文档维护
- 自动化文档生成
- 示例代码维护

## 十一、安全实现
### 1. 跨域安全
- CORS配置
  - 允许源配置
  - 请求方法限制

### 2. 日志记录
- 操作日志
  - 用户行为跟踪
  - 系统变更记录
- 日志存储
  - 本地日志轮转
  - 定期备份