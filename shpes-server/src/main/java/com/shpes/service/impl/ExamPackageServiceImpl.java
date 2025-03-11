package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamItem;
import com.shpes.entity.ExamPackage;
import com.shpes.entity.ExamPackageItem;
import com.shpes.mapper.ExamItemMapper;
import com.shpes.mapper.ExamPackageItemMapper;
import com.shpes.mapper.ExamPackageMapper;
import com.shpes.service.ExamPackageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检套餐服务实现类
 */
@Service
public class ExamPackageServiceImpl implements ExamPackageService {

    @Autowired
    private ExamPackageMapper packageMapper;

    @Autowired
    private ExamPackageItemMapper packageItemMapper;

    @Autowired
    private ExamItemMapper itemMapper;

    @Override
    public Page<ExamPackage> getPackagePage(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<ExamPackage> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(ExamPackage::getName, keyword)
                    .or()
                    .like(ExamPackage::getCode, keyword);
        }
        wrapper.orderByAsc(ExamPackage::getSort);
        return packageMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<ExamPackage> getAllPackages() {
        LambdaQueryWrapper<ExamPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPackage::getStatus, 1)
                .orderByAsc(ExamPackage::getSort);
        return packageMapper.selectList(wrapper);
    }

    @Override
    public ExamPackage getPackage(Long id) {
        return packageMapper.selectById(id);
    }

    @Override
    public void createPackage(ExamPackage examPackage) {
        packageMapper.insert(examPackage);
    }

    @Override
    public void updatePackage(ExamPackage examPackage) {
        packageMapper.updateById(examPackage);
    }

    @Override
    public void deletePackage(Long id) {
        // 删除套餐
        packageMapper.deleteById(id);
        // 删除套餐项目关联
        LambdaQueryWrapper<ExamPackageItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPackageItem::getPackageId, id);
        packageItemMapper.delete(wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ExamPackage examPackage = new ExamPackage();
        examPackage.setId(id);
        examPackage.setStatus(status);
        packageMapper.updateById(examPackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configureItems(Long packageId, List<Long> itemIds) {
        // 删除原有关联
        LambdaQueryWrapper<ExamPackageItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPackageItem::getPackageId, packageId);
        packageItemMapper.delete(wrapper);

        // 添加新关联
        if (itemIds != null && !itemIds.isEmpty()) {
            List<ExamPackageItem> items = new ArrayList<>();
            for (int i = 0; i < itemIds.size(); i++) {
                ExamPackageItem item = new ExamPackageItem();
                item.setPackageId(packageId);
                item.setItemId(itemIds.get(i));
                item.setSort(i + 1);
                items.add(item);
            }
            items.forEach(packageItemMapper::insert);
        }
    }

    @Override
    public List<ExamItem> getPackageItems(Long packageId) {
        // 查询套餐项目关联
        LambdaQueryWrapper<ExamPackageItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPackageItem::getPackageId, packageId)
                .orderByAsc(ExamPackageItem::getSort);
        List<ExamPackageItem> packageItems = packageItemMapper.selectList(wrapper);

        // 获取项目ID列表
        List<Long> itemIds = packageItems.stream()
                .map(ExamPackageItem::getItemId)
                .collect(Collectors.toList());

        if (itemIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询项目详情
        LambdaQueryWrapper<ExamItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(ExamItem::getId, itemIds);
        return itemMapper.selectList(itemWrapper);
    }
} 