package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.enums.ExamCategoryEnum;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamItem;
import com.shpes.entity.SysDepartment;
import com.shpes.mapper.ExamItemMapper;
import com.shpes.mapper.SysDepartmentMapper;
import com.shpes.service.ExamItemService;
import com.shpes.vo.ExamItemVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检项目服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExamItemServiceImpl extends ServiceImpl<ExamItemMapper, ExamItem> implements ExamItemService {

    @Autowired
    private SysDepartmentMapper departmentMapper;

    @Override
    public CommonPage<ExamItemVO> getItemPage(Integer pageNum, Integer pageSize, String keyword) {
        // 参数验证
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 构建查询条件
        LambdaQueryWrapper<ExamItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(keyword), ExamItem::getName, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), ExamItem::getCode, keyword)
                .orderByAsc(ExamItem::getId);

        // 执行分页查询，使用 IService 的 page 方法
        Page<ExamItem> page = page(new Page<>(pageNum, pageSize), wrapper);

        // 转换记录列表
        List<ExamItemVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 返回通用分页对象
        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public void createItem(ExamItem item) {
        // 参数验证
        if (item == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isBlank(item.getCode())) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isBlank(item.getName())) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (item.getDeptId() == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }

        // 检查项目编码是否已存在
        if (isItemCodeExists(item.getCode())) {
            throw new ApiException(ResultCode.EXAM_ITEM_USED);
        }

        // 检查执行科室是否存在
        if (!isDepartmentExists(item.getDeptId())) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        // 设置默认值
        item.setStatus(1); // 1-启用

        baseMapper.insert(item);
    }

    @Override
    public void updateItem(ExamItem item) {
        // 参数验证
        if (item == null || item.getId() == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isBlank(item.getCode())) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isBlank(item.getName())) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (item.getDeptId() == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }

        // 检查项目是否存在
        ExamItem existingItem = baseMapper.selectById(item.getId());
        if (existingItem == null) {
            throw new ApiException(ResultCode.EXAM_ITEM_NOT_EXIST);
        }

        // 如果修改了项目编码，检查新编码是否已存在
        if (!existingItem.getCode().equals(item.getCode()) && isItemCodeExists(item.getCode())) {
            throw new ApiException(ResultCode.EXAM_ITEM_USED);
        }

        // 检查执行科室是否存在
        if (!isDepartmentExists(item.getDeptId())) {
            throw new ApiException(ResultCode.DEPARTMENT_NOT_EXIST);
        }

        baseMapper.updateById(item);
    }

    @Override
    public ExamItemVO getItemReference(Long id) {
        // 获取项目
        ExamItem item = getItem(id);
        // 转换为VO
        return convertToVO(item);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 参数验证
        if (id == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }

        ExamItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new ApiException(ResultCode.EXAM_ITEM_NOT_EXIST);
        }

        item.setStatus(status);
        baseMapper.updateById(item);
    }

    @Override
    public void deleteItem(Long id) {
        // 参数验证
        if (id == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }

        // 检查项目是否存在
        ExamItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new ApiException(ResultCode.EXAM_ITEM_NOT_EXIST);
        }

        // 检查项目是否被使用
        if (isItemUsed(id)) {
            throw new ApiException(ResultCode.EXAM_ITEM_USED);
        }

        baseMapper.deleteById(id);
    }

    @Override
    public ExamItem getItem(Long id) {
        // 参数验证
        if (id == null) {
            throw new ApiException(ResultCode.VALIDATE_FAILED);
        }

        ExamItem item = baseMapper.selectById(id);
        if (item == null) {
            throw new ApiException(ResultCode.EXAM_ITEM_NOT_EXIST);
        }
        return item;
    }

    @Override
    public List<ExamItemVO> getAvailableItems() {
        // 获取所有状态为启用的项目
        LambdaQueryWrapper<ExamItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamItem::getStatus, 1);  // 1表示启用状态
        
        // 查询数据库
        List<ExamItem> items = baseMapper.selectList(wrapper);
        
        // 转换为VO列表
        return items.stream().map(this::convertToVO).collect(Collectors.toList());
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
     * 检查项目是否被使用
     */
    private boolean isItemUsed(Long id) {
        // TODO: 实现检查项目是否被使用的逻辑
        // 1. 检查是否被套餐使用
        // 2. 检查是否被体检记录使用
        return false;
    }

    /**
     * 将ExamItem转为ExamItemVO
     */
    private ExamItemVO convertToVO(ExamItem item) {
        ExamItemVO vo = new ExamItemVO();
        BeanUtils.copyProperties(item, vo);

        // 设置分类名称
        if (item.getCategoryId() != null) {
            ExamCategoryEnum category = ExamCategoryEnum.getByValue(item.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getLabel());
            }
        }

        // 获取科室信息
        SysDepartment department = departmentMapper.selectById(item.getDeptId());
        if (department != null) {
            vo.setDeptName(department.getDeptName());
        }

        return vo;
    }
}