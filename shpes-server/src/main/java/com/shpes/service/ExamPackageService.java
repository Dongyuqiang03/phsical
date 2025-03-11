package com.shpes.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamItem;
import com.shpes.entity.ExamPackage;

import java.util.List;

/**
 * 体检套餐服务接口
 */
public interface ExamPackageService {

    /**
     * 分页查询体检套餐
     */
    Page<ExamPackage> getPackagePage(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 获取所有体检套餐
     */
    List<ExamPackage> getAllPackages();

    /**
     * 根据ID获取体检套餐
     */
    ExamPackage getPackage(Long id);

    /**
     * 创建体检套餐
     */
    void createPackage(ExamPackage examPackage);

    /**
     * 更新体检套餐
     */
    void updatePackage(ExamPackage examPackage);

    /**
     * 删除体检套餐
     */
    void deletePackage(Long id);

    /**
     * 更新体检套餐状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 配置套餐项目
     */
    void configureItems(Long packageId, List<Long> itemIds);

    /**
     * 获取套餐项目列表
     */
    List<ExamItem> getPackageItems(Long packageId);
} 