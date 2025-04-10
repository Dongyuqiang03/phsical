package com.shpes.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamResult;
import com.shpes.mapper.ExamResultMapper;
import com.shpes.service.ExamResultService;
import com.shpes.utils.SecurityUtils;
import com.shpes.vo.ExamResultVO;
import com.shpes.dto.ExamResultBatchDTO;
import com.shpes.dto.ExamResultBatchDTO.ExamResultItemDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * 体检结果服务实现类
 */
@Service
public class ExamResultServiceImpl extends ServiceImpl<ExamResultMapper, ExamResult> implements ExamResultService {


    @Override
    public List<ExamResultVO> getResultsByRecordId(Long recordId) {
        return baseMapper.selectResultsByRecordId(recordId);
    }

    @Override
    public CommonPage<ExamResultVO> getResultPage(Integer pageNum, Integer pageSize, Long recordId,
                                                  Long itemId, Integer resultType, Integer reviewStatus) {
        Page<ExamResult> page = baseMapper.selectResultPage(new Page<>(pageNum, pageSize), 
                recordId, itemId, resultType, reviewStatus);
        return CommonPage.restPage(convertToVOList(page.getRecords()), 
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamResultVO createResult(ExamResult result) {
        result.setCreateTime(LocalDateTime.now());
        result.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(result);
        return convertToVO(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExamResultVO> createResults(List<ExamResult> results) {
        LocalDateTime now = LocalDateTime.now();
        results.forEach(result -> {
            result.setCreateTime(now);
            result.setUpdateTime(now);
            baseMapper.insert(result);
        });
        return convertToVOList(results);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamResultVO updateResult(ExamResult result) {
        result.setUpdateTime(LocalDateTime.now());
        if (!updateById(result)) {
            throw new ApiException(ResultCode.EXAM_RESULT_NOT_EXIST);
        }
        return convertToVO(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResult(Long id) {
        if (!removeById(id)) {
            throw new ApiException(ResultCode.EXAM_RESULT_NOT_EXIST);
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamResultVO reviewResult(Long id, String suggestion) {
        ExamResult result = getById(id);
        if (result == null) {
            throw new ApiException(ResultCode.EXAM_RESULT_NOT_EXIST);
        }
        
        // 设置复核信息
        result.setReviewerId(SecurityUtils.getCurrentUserId());
        result.setSuggestion(suggestion);
        result.setStatus(2);  // 设置为已复核状态
        result.setUpdateTime(LocalDateTime.now());
        updateById(result);
        
        return convertToVO(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExamResultVO> reviewResults(List<Long> ids) {
        List<ExamResult> results = listByIds(ids);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        
        results.forEach(result -> {
            result.setReviewerId(currentUserId);
            result.setStatus(2);  // 设置为已复核状态
            result.setUpdateTime(now);
            updateById(result);
        });
        
        return convertToVOList(results);
    }

    @Override
    public String exportReport(Long recordId) {
        // TODO: 实现导出报告逻辑
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExamResultVO> createResultsFromBatchDTO(ExamResultBatchDTO batchDTO) {
        List<ExamResult> results = new ArrayList<>();
        
        // 将DTO转换为ExamResult列表
        if (batchDTO.getItems() != null && !batchDTO.getItems().isEmpty()) {
            for (ExamResultItemDTO item : batchDTO.getItems()) {
                ExamResult result = new ExamResult();
                result.setRecordId(batchDTO.getId());  // 设置体检记录ID
                result.setItemId(item.getItemId());   // 设置体检项目ID
                result.setValue(item.getResult());    // 设置检查结果值
                
                // 转换状态：NORMAL -> 0, ABNORMAL -> 1
                result.setAbnormal("ABNORMAL".equals(item.getStatus()) ? 1 : 0);
                
                // 设置医生建议/分析
                result.setSuggestion(item.getAnalysis());
                
                // 设置操作人员信息（当前登录用户）
                result.setDoctorId(SecurityUtils.getCurrentUserId());
                
                // 设置状态为待复核
                result.setStatus(1);
                
                results.add(result);
            }
        }
        
        // 调用现有方法批量保存结果
        return createResults(results);
    }

    private ExamResultVO convertToVO(ExamResult result) {
        if (result == null) {
            return null;
        }
        ExamResultVO vo = new ExamResultVO();
        BeanUtils.copyProperties(result, vo);
        return vo;
    }

    private List<ExamResultVO> convertToVOList(List<ExamResult> results) {
        return results.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
}