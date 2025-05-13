package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamPackage;
import com.shpes.mapper.ExamItemMapper;
import com.shpes.mapper.ExamPackageMapper;
import com.shpes.service.ExamItemService;
import com.shpes.service.ExamPackageService;
import com.shpes.vo.ExamItemVO;
import com.shpes.vo.ExamPackageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExamPackageServiceImpl extends ServiceImpl<ExamPackageMapper, ExamPackage> implements ExamPackageService {

    @Autowired
    private ExamItemMapper itemMapper;

    @Override
    public CommonPage<ExamPackageVO> getPackagePage(Integer pageNum, Integer pageSize, String name, Integer gender, Integer status) {
        LambdaQueryWrapper<ExamPackage> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(ExamPackage::getName, name);
        }
        // 移除 type 相关的查询条件，因为表中不存在该字段
        if (gender != null) {
            wrapper.eq(ExamPackage::getGender, gender);
        }
        if (status != null) {
            wrapper.eq(ExamPackage::getStatus, status);
        }
        wrapper.orderByDesc(ExamPackage::getCreateTime);

        Page<ExamPackage> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ExamPackageVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public ExamPackageVO getPackageById(Long id) {
        ExamPackage examPackage = getById(id);
        if (examPackage == null) {
            throw new ApiException(ResultCode.EXAM_PACKAGE_NOT_EXIST);
        }
        return convertToVO(examPackage);
    }

    @Override
    public List<ExamPackageVO> getAvailablePackages(Integer gender) {
        LambdaQueryWrapper<ExamPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPackage::getStatus, 1);
        if (gender != null) {
            wrapper.and(w -> w.eq(ExamPackage::getGender, 0)
                    .or()
                    .eq(ExamPackage::getGender, gender));
        }
        wrapper.orderByAsc(ExamPackage::getId);

        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamPackageVO createPackage(ExamPackage examPackage) {
        save(examPackage);
        return convertToVO(examPackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamPackageVO updatePackage(ExamPackage examPackage) {
        if (!updateById(examPackage)) {
            throw new ApiException(ResultCode.EXAM_PACKAGE_NOT_EXIST);
        }
        return convertToVO(examPackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePackage(Long id) {
        if (!removeById(id)) {
            throw new ApiException(ResultCode.EXAM_PACKAGE_NOT_EXIST);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamPackageVO updateStatus(Long id, Integer status) {
        ExamPackage examPackage = getById(id);
        if (examPackage == null) {
            throw new ApiException(ResultCode.EXAM_PACKAGE_NOT_EXIST);
        }
        examPackage.setStatus(status);
        updateById(examPackage);
        return convertToVO(examPackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamPackageVO configureItems(Long packageId, List<Long> itemIds) {
        ExamPackage examPackage = getById(packageId);
        if (examPackage == null) {
            throw new ApiException(ResultCode.EXAM_PACKAGE_NOT_EXIST);
        }

        // 删除原有配置
        baseMapper.deletePackageItems(packageId);

        // 添加新配置并计算总价
        if (itemIds != null && !itemIds.isEmpty()) {
            // 使用Mapper直接查询价格并计算总和
            Integer totalPrice = itemMapper.selectItemsPriceSum(itemIds);
            
            // 更新套餐价格
            examPackage.setPrice(totalPrice != null ? totalPrice : 0);
            updateById(examPackage);
            
            // 插入套餐项目关联
            baseMapper.insertPackageItems(packageId, itemIds);
        } else {
            // 如果没有项目，价格设为0
            examPackage.setPrice(0);
            updateById(examPackage);
        }

        return convertToVO(examPackage);
    }

    @Override
    public List<ExamItemVO> getPackageItems(Long packageId) {
        return baseMapper.selectPackageItems(packageId);
    }

    @Override
    public Object getPackageStats() {
        return baseMapper.selectPackageStats();
    }
    
    @Override
    public Long getDepartmentIdByPackageId(Long packageId) {
        // 这里主要是获取套餐对应的科室ID
        // 实现可能有多种方式，根据实际业务逻辑选择最合适的
        
        // 获取套餐
        ExamPackage examPackage = getById(packageId);
        if (examPackage == null) {
            return null;
        }
        
        // 通过套餐关联的检查项目找到主要科室
        // 这里假设我们取第一个项目的科室作为套餐的主科室
        List<ExamItemVO> items = getPackageItems(packageId);
        if (items != null && !items.isEmpty()) {
            for (ExamItemVO item : items) {
                if (item.getDeptId() != null) {
                    return item.getDeptId();
                }
            }
        }
        
        // 如果没有找到科室，默认返回一个常用科室ID（如体检中心）
        // 在实际应用中，可能需要根据业务规则确定一个默认值
        return 1L; // 假设ID为1的科室是体检中心
    }

    /**
     * 将实体转换为VO
     */
    private ExamPackageVO convertToVO(ExamPackage examPackage) {
        if (examPackage == null) {
            return null;
        }
        ExamPackageVO vo = new ExamPackageVO();
        BeanUtils.copyProperties(examPackage, vo);
        
        // 查询并设置套餐包含的项目列表
        List<ExamItemVO> items = baseMapper.selectPackageItems(examPackage.getId());
        vo.setItems(items);
        // 设置项目数量
        vo.setItemCount(items != null ? items.size() : 0);
        
        return vo;
    }
}