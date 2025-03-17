package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.SysDepartment;
import com.shpes.entity.SysUser;
import com.shpes.mapper.SysDepartmentMapper;
import com.shpes.mapper.SysUserMapper;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.DepartmentVO;
import com.shpes.vo.UserSimpleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public CommonPage<DepartmentVO> getDepartmentPage(Integer pageNum, Integer pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(keyword), SysDepartment::getDeptName, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), SysDepartment::getDeptCode, keyword)
                .orderByAsc(SysDepartment::getSort);

        // 执行分页查询
        Page<SysDepartment> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectPage(page, wrapper);

        // 转换记录列表
        List<DepartmentVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 返回通用分页对象
        return CommonPage.restPage(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public List<DepartmentVO> getAllDepartments() {
        List<SysDepartment> departments = baseMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .orderByAsc(SysDepartment::getSort));
        
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDepartment(SysDepartment department) {
        // 检查部门编码是否已存在
        if (isDepartmentCodeExists(department.getDeptCode())) {
            throw new ApiException(ResultCode.DEPARTMENT_USED);
        }

        baseMapper.insert(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(SysDepartment department) {
        // 检查部门是否存在
        SysDepartment existingDepartment = baseMapper.selectById(department.getId());
        if (existingDepartment == null) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        // 如果修改了部门编码，检查新编码是否已存在
        if (!existingDepartment.getDeptCode().equals(department.getDeptCode()) && isDepartmentCodeExists(department.getDeptCode())) {
            throw new ApiException(ResultCode.DEPARTMENT_USED);
        }

        baseMapper.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        // 检查部门是否存在
        SysDepartment department = baseMapper.selectById(id);
        if (department == null) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        // 检查部门下是否有人员
        if (hasDepartmentUsers(id)) {
            throw new ApiException(ResultCode.DEPARTMENT_USED);
        }

        baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        SysDepartment department = baseMapper.selectById(id);
        if (department == null) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        department.setStatus(status);
        baseMapper.updateById(department);
    }

    @Override
    public List<UserSimpleVO> getDepartmentUsers(Long id) {
        // 检查部门是否存在
        SysDepartment department = baseMapper.selectById(id);
        if (department == null) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        // 查询部门下的用户
        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, id)
                .orderByAsc(SysUser::getId));

        return users.stream()
                .map(this::convertToUserSimpleVO)
                .collect(Collectors.toList());
    }

    /**
     * 检查部门编码是否已存在
     */
    private boolean isDepartmentCodeExists(String code) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getDeptCode, code)) > 0;
    }

    /**
     * 检查用户是否存在
     */
    private boolean isUserExists(Long userId) {
        return userMapper.selectById(userId) != null;
    }

    /**
     * 检查部门下是否有人员
     */
    private boolean hasDepartmentUsers(Long departmentId) {
        return userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, departmentId)) > 0;
    }

    /**
     * 将实体对象转换为VO对象
     */
    private DepartmentVO convertToVO(SysDepartment department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);

        // 获取部门人员数量
        vo.setUserCount(userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, department.getId())).intValue());

        return vo;
    }

    /**
     * 将用户实体转换为简单VO对象
     */
    private UserSimpleVO convertToUserSimpleVO(SysUser user) {
        UserSimpleVO vo = new UserSimpleVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public String getDepartmentNameById(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        SysDepartment department = baseMapper.selectById(departmentId);
        return department != null ? department.getDeptName() : null;
    }
}