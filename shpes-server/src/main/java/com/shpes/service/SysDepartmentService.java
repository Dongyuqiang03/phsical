package com.shpes.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.dto.DepartmentDTO;
import com.shpes.entity.SysDepartment;
import com.shpes.vo.DepartmentVO;

import java.util.List;

/**
 * 部门服务接口
 */
public interface SysDepartmentService extends IService<SysDepartment> {
    
    /**
     * 分页查询部门列表
     *
     * @param page     分页参数
     * @param deptName 部门名称
     * @param status   状态
     * @return 分页结果
     */
    Page<DepartmentVO> getDepartmentPage(Page<SysDepartment> page, String deptName, Integer status);
    
    /**
     * 根据用户类型获取部门列表
     * @param userType 用户类型：1-医护人员，2-教职工，3-学生，null-查询所有
     */
    List<DepartmentVO> getDepartmentList(Integer userType);
    
    /**
     * 创建部门
     */
    Boolean createDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 更新部门
     */
    Boolean updateDepartment(SysDepartment department);
    
    /**
     * 删除部门
     */
    Boolean deleteDepartment(Long id);
    
    /**
     * 批量更新部门状态
     */
    Boolean batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 根据部门ID获取部门名称
     *
     * @param departmentId 部门ID
     * @return 部门名称
     */
    String getDepartmentNameById(Long departmentId);
}