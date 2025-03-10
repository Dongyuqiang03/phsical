package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.entity.ExamItem;
import com.shpes.mapper.ExamItemMapper;
import com.shpes.service.ExamItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ExamItemServiceImpl extends ServiceImpl<ExamItemMapper, ExamItem> implements ExamItemService {

    @Override
    public IPage<ExamItem> getExamItemPage(int page, int size, String itemName, Long categoryId) {
        Page<ExamItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ExamItem> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(itemName)) {
            wrapper.like(ExamItem::getItemName, itemName);
        }
        if (categoryId != null) {
            wrapper.eq(ExamItem::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(ExamItem::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean addExamItem(ExamItem examItem) {
        return this.save(examItem);
    }

    @Override
    public boolean updateExamItem(ExamItem examItem) {
        return this.updateById(examItem);
    }

    @Override
    public boolean deleteExamItem(Long id) {
        return this.removeById(id);
    }

    @Override
    public ExamItem getExamItemById(Long id) {
        return this.getById(id);
    }
}