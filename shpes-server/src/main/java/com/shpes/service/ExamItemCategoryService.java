package com.shpes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shpes.common.api.CommonPage;
import com.shpes.entity.ExamItemCategory;
import com.shpes.vo.ExamItemCategoryVO;

import java.util.List;

/**
 * 体检项目分类服务接口
 */
public interface ExamItemCategoryService extends IService<ExamItemCategory> {
    
    /**
     * 分页查询分类列表
     *
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @param name     分类名称
     * @param code     分类编码
     * @param status   状态
     * @return 分页结果
     */
    CommonPage<ExamItemCategoryVO> getCategoryPage(Integer pageNum, Integer pageSize, String name, String code, Integer status);

    /**
     * 创建分类
     *
     * @param categoryVO 分类信息
     * @return 是否创建成功
     */
    boolean createCategory(ExamItemCategoryVO categoryVO);

    /**
     * 更新分类
     *
     * @param categoryVO 分类信息
     * @return 是否更新成功
     */
    boolean updateCategory(ExamItemCategoryVO categoryVO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long id);
}