package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamItemCategory;
import com.shpes.mapper.ExamItemCategoryMapper;
import com.shpes.service.ExamItemCategoryService;
import com.shpes.vo.ExamItemCategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检项目分类服务实现类
 */
@Service
public class ExamItemCategoryServiceImpl extends ServiceImpl<ExamItemCategoryMapper, ExamItemCategory> implements ExamItemCategoryService {

    @Override
    public CommonPage<ExamItemCategoryVO> getCategoryPage(Integer pageNum, Integer pageSize, String name, String code, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<ExamItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(ExamItemCategory::getName, name);
        }
        if (StringUtils.hasText(code)) {
            queryWrapper.like(ExamItemCategory::getCode, code);
        }
        if (status != null) {
            queryWrapper.eq(ExamItemCategory::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(ExamItemCategory::getCreateTime);
        
        // 执行分页查询
        Page<ExamItemCategory> page = new Page<>(pageNum, pageSize);
        Page<ExamItemCategory> result = this.page(page, queryWrapper);
        
        // 转换为VO
        List<ExamItemCategoryVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建分页结果
        CommonPage<ExamItemCategoryVO> commonPage = new CommonPage<>();
        commonPage.setPageNum(result.getCurrent());
        commonPage.setPageSize(result.getSize());
        commonPage.setTotal(result.getTotal());
        commonPage.setRecords(voList);
        return commonPage;
    }

    @Override
    public List<ExamItemCategoryVO> getAllCategories() {
        // 查询所有启用的分类
        LambdaQueryWrapper<ExamItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamItemCategory::getStatus, 1)
                   .orderByDesc(ExamItemCategory::getCreateTime);
        
        return this.list(queryWrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamItemCategoryVO getCategoryById(Long id) {
        ExamItemCategory category = this.getById(id);
        return category != null ? convertToVO(category) : null;
    }

    @Override
    public boolean createCategory(ExamItemCategoryVO categoryVO) {
        // 检查编码是否已存在
        if (isCodeExists(categoryVO.getCode())) {
            return false;
        }
        
        ExamItemCategory category = convertToEntity(categoryVO);
        return this.save(category);
    }

    @Override
    public boolean updateCategory(ExamItemCategoryVO categoryVO) {
        // 检查编码是否已存在（排除自身）
        if (isCodeExists(categoryVO.getCode(), categoryVO.getId())) {
            return false;
        }
        
        ExamItemCategory category = convertToEntity(categoryVO);
        return this.updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        return this.removeById(id);
    }

    /**
     * 检查编码是否已存在
     */
    private boolean isCodeExists(String code) {
        return isCodeExists(code, null);
    }

    /**
     * 检查编码是否已存在（排除指定ID的记录）
     */
    private boolean isCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<ExamItemCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamItemCategory::getCode, code);
        if (excludeId != null) {
            queryWrapper.ne(ExamItemCategory::getId, excludeId);
        }
        return this.count(queryWrapper) > 0;
    }

    /**
     * 将实体转换为VO
     */
    private ExamItemCategoryVO convertToVO(ExamItemCategory category) {
        if (category == null) {
            return null;
        }
        ExamItemCategoryVO vo = new ExamItemCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    /**
     * 将VO转换为实体
     */
    private ExamItemCategory convertToEntity(ExamItemCategoryVO vo) {
        if (vo == null) {
            return null;
        }
        ExamItemCategory entity = new ExamItemCategory();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
} 