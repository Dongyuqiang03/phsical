package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.entity.SysDepartment;
import com.shpes.mapper.SysDepartmentMapper;
import com.shpes.service.SysDepartmentService;
import com.shpes.vo.DepartmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    private final SysDepartmentMapper departmentMapper;

    @Override
    public Page<DepartmentVO> getDepartmentPage(Page<SysDepartment> page, String deptName, Integer status) {
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(deptName), SysDepartment::getDeptName, deptName)
                .eq(status != null, SysDepartment::getStatus, status)
                .orderByAsc(SysDepartment::getId);
        
        Page<SysDepartment> resultPage = this.page(page, wrapper);
        Page<DepartmentVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage, "records");
        
        List<DepartmentVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public List<DepartmentVO> getDepartmentList() {
        List<SysDepartment> departments = departmentMapper.getDepartmentList();
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDepartment(SysDepartment department) {
        return this.save(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(SysDepartment department) {
        return this.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDepartment(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<Long> ids, Integer status) {
        return departmentMapper.batchUpdateStatus(ids, status) > 0;
    }

    @Override
    public String getDepartmentNameById(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        SysDepartment department = this.getById(departmentId);
        return department != null ? department.getDeptName() : null;
    }

    /**
     * 将实体转换为VO对象
     */
    private DepartmentVO convertToVO(SysDepartment department) {
        if (department == null) {
            return null;
        }
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        return vo;
    }
}