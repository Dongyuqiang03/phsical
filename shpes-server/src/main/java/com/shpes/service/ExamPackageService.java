package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamPackage;
import com.shpes.vo.ExamItemVO;
import com.shpes.vo.ExamPackageVO;

import java.util.List;

/**
 * 体检套餐服务接口
 */
public interface ExamPackageService {

    /**
     * 分页查询体检套餐
     */
    CommonPage<ExamPackageVO> getPackagePage(Integer pageNum, Integer pageSize, String name, 
            Integer gender, Integer status);

    /**
     * 根据ID获取体检套餐
     */
    ExamPackageVO getPackageById(Long id);

    /**
     * 获取所有可用套餐
     */
    List<ExamPackageVO> getAvailablePackages(Integer gender);

    /**
     * 创建体检套餐
     */
    ExamPackageVO createPackage(ExamPackage examPackage);

    /**
     * 更新体检套餐
     */
    ExamPackageVO updatePackage(ExamPackage examPackage);

    /**
     * 删除体检套餐
     */
    void deletePackage(Long id);

    /**
     * 更新体检套餐状态
     */
    ExamPackageVO updateStatus(Long id, Integer status);

    /**
     * 配置套餐项目
     */
    ExamPackageVO configureItems(Long packageId, List<Long> itemIds);

    /**
     * 获取套餐项目列表
     */
    List<ExamItemVO> getPackageItems(Long packageId);

    /**
     * 获取套餐统计信息
     */
    Object getPackageStats();
}