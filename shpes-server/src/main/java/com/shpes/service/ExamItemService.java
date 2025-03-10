package com.shpes.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.entity.ExamItem;

public interface ExamItemService extends IService<ExamItem> {

    /**
     * 分页查询体检项目
     * @param page 页码
     * @param size 每页大小
     * @param itemName 项目名称（可选）
     * @param categoryId 分类ID（可选）
     * @return 分页结果
     */
    IPage<ExamItem> getExamItemPage(int page, int size, String itemName, Long categoryId);

    /**
     * 添加体检项目
     * @param examItem 体检项目信息
     * @return 是否成功
     */
    boolean addExamItem(ExamItem examItem);

    /**
     * 更新体检项目
     * @param examItem 体检项目信息
     * @return 是否成功
     */
    boolean updateExamItem(ExamItem examItem);

    /**
     * 删除体检项目
     * @param id 体检项目ID
     * @return 是否成功
     */
    boolean deleteExamItem(Long id);

    /**
     * 获取体检项目详情
     * @param id 体检项目ID
     * @return 体检项目信息
     */
    ExamItem getExamItemById(Long id);
}