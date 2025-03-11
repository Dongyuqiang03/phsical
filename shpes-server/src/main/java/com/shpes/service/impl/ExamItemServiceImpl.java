package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamItem;
import com.shpes.entity.SysDepartment;
import com.shpes.mapper.ExamItemMapper;
import com.shpes.mapper.SysDepartmentMapper;
import com.shpes.service.ExamItemService;
import com.shpes.vo.ExamItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检项目服务实现类
 */
@Service
public class ExamItemServiceImpl extends ServiceImpl<ExamItemMapper, ExamItem> implements ExamItemService {

    @Autowired
    private SysDepartmentMapper departmentMapper;

    @Override
    public CommonPage<ExamItemVO> getItemPage(Integer pageNum, Integer pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<ExamItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(keyword), ExamItem::getName, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), ExamItem::getCode, keyword)
                .orderByAsc(ExamItem::getSort);

        // 执行分页查询
        Page<ExamItem> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectPage(page, wrapper);

        // 转换记录列表
        List<ExamItemVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 返回通用分页对象
        return CommonPage.restPage(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createItem(ExamItem item) {
        // 检查项目编码是否已存在
        if (isItemCodeExists(item.getCode())) {
            throw new ApiException(ResultCode.ITEM_CODE_EXISTS);
        }

        // 检查执行科室是否存在
        if (!isDepartmentExists(item.getDepartmentId())) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_FOUND);
        }

        baseMapper.insert(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(ExamItem item) {
        // 检查项目是否存在
        ExamItem existingItem = baseMapper.selectById(item.getId());
        if (existingItem == null) {
            throw new ApiException(ResultCode.ITEM_NOT_FOUND);
        }

        // 如果修改了项目编码，检查新编码是否已存在
        if (!existingItem.getCode().equals(item.getCode()) && isItemCodeExists(item.getCode())) {
            throw new ApiException(ResultCode.ITEM_CODE_EXISTS);
        }

        // 检查执行科室是否存在
        if (!isDepartmentExists(item.getDepartmentId())) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_FOUND);
        }

        baseMapper.updateById(item);
    }

    @Override
    public ExamItemVO getItemReference(Long id) {
        ExamItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new ApiException(ResultCode.ITEM_NOT_FOUND);
        }
        return convertToVO(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        ExamItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new ApiException(ResultCode.ITEM_NOT_FOUND);
        }

        item.setStatus(status);
        baseMapper.updateById(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long id) {
        // TODO: 检查项目是否被使用
        baseMapper.deleteById(id);
    }

    /**
     * 检查项目编码是否已存在
     */
    private boolean isItemCodeExists(String code) {
        return baseMapper.selectCount(new LambdaQueryWrapper<ExamItem>()
                .eq(ExamItem::getCode, code)) > 0;
    }

    /**
     * 检查科室是否存在
     */
    private boolean isDepartmentExists(Long departmentId) {
        return departmentMapper.selectById(departmentId) != null;
    }

    /**
     * 将实体对象转换为VO对象
     */
    private ExamItemVO convertToVO(ExamItem item) {
        ExamItemVO vo = new ExamItemVO();
        BeanUtils.copyProperties(item, vo);

        // 设置分类名称
        if (item.getCategory() != null) {
            vo.setCategoryName(item.getCategory().getDesc());
        }

        // 获取科室信息
        SysDepartment department = departmentMapper.selectById(item.getDepartmentId());
        if (department != null) {
            vo.setDepartmentName(department.getName());
        }

        return vo;
    }
}