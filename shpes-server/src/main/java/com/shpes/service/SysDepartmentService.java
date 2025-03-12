package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.SysDepartment;
import com.shpes.entity.SysUser;
import com.shpes.vo.DepartmentVO;
import com.shpes.vo.UserSimpleVO;

import java.util.List;

/**
 * 部门服务接口
 */
public interface SysDepartmentService {

    /**
     * 分页查询部门
     */
    CommonPage<DepartmentVO> getDepartmentPage(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 获取所有部门
     */
    List<DepartmentVO> getAllDepartments();

    /**
     * 根据ID获取部门
     */
    SysDepartment getDepartment(Long id);

    /**
     * 创建部门
     */
    void createDepartment(SysDepartment department);

    /**
     * 更新部门
     */
    void updateDepartment(SysDepartment department);

    /**
     * 删除部门
     */
    void deleteDepartment(Long id);

    /**
     * 更新部门状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取部门人员列表
     */
    List<UserSimpleVO> getDepartmentUsers(Long id);

    /**
     * 根据部门ID获取部门名称
     */
    String getDepartmentNameById(Long departmentId);
}