package com.shpes.service;

import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamItem;
import com.shpes.vo.ExamItemVO;

import java.util.List;

/**
 * 体检项目服务接口
 */
public interface ExamItemService {

    /**
     * 分页查询项目
     */
    CommonPage<ExamItemVO> getItemPage(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 获取所有体检项目
     */
    List<ExamItem> getAllItems();

    /**
     * 根据ID获取体检项目
     */
    ExamItem getItem(Long id);

    /**
     * 创建体检项目
     */
    void createItem(ExamItem item);

    /**
     * 更新体检项目
     */
    void updateItem(ExamItem item);

    /**
     * 删除体检项目
     */
    void deleteItem(Long id);

    /**
     * 更新体检项目状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取项目参考值
     */
    ExamItemVO getItemReference(Long id);
}