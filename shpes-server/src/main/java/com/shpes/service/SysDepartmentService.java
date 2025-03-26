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
     * 获取部门列表
     *
     * @return 部门列表
     */
    List<DepartmentVO> getDepartmentList();

    /**
     * 创建部门
     *
     * @param departmentDTO 部门信息
     * @return 是否成功
     */
    boolean createDepartment(DepartmentDTO departmentDTO);

    /**
     * 更新部门
     *
     * @param department 部门信息
     * @return 是否成功
     */
    boolean updateDepartment(SysDepartment department);

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 是否成功
     */
    boolean deleteDepartment(Long id);

    /**
     * 批量更新部门状态
     *
     * @param ids    部门ID列表
     * @param status 状态
     * @return 是否成功
     */
    boolean batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 根据部门ID获取部门名称
     *
     * @param departmentId 部门ID
     * @return 部门名称
     */
    String getDepartmentNameById(Long departmentId);
}