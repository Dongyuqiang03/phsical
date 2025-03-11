package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.common.api.ApiException;
import com.shpes.common.api.ResultCode;
import com.shpes.entity.ExamResult;
import com.shpes.mapper.ExamResultMapper;
import com.shpes.service.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检结果服务实现类
 */
@Service
public class ExamResultServiceImpl implements ExamResultService {

    @Autowired
    private ExamResultMapper resultMapper;

    @Override
    public List<ExamResult> getRecordResults(Long recordId) {
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getRecordId, recordId)
                .orderByAsc(ExamResult::getItemId);
        return resultMapper.selectList(wrapper);
    }

    @Override
    public ExamResult getResult(Long id) {
        ExamResult result = resultMapper.selectById(id);
        if (result == null) {
            throw new ApiException(ResultCode.EXAM_RESULT_NOT_EXIST);
        }
        return result;
    }

    @Override
    @Transactional
    public void createResults(List<ExamResult> results) {
        for (ExamResult result : results) {
            result.setStatus(1); // 待复核
            result.setCreateTime(LocalDateTime.now());
            result.setUpdateTime(LocalDateTime.now());
            resultMapper.insert(result);
        }
    }

    @Override
    public void updateResult(ExamResult result) {
        ExamResult existingResult = getResult(result.getId());
        if (existingResult.getStatus() == 2) {
            throw new ApiException(ResultCode.EXAM_RESULT_ALREADY_SUBMITTED);
        }

        result.setStatus(1); // 重置为待复核
        result.setUpdateTime(LocalDateTime.now());
        resultMapper.updateById(result);
    }

    @Override
    public void reviewResult(Long id, Long reviewerId) {
        ExamResult result = getResult(id);
        if (result.getStatus() == 2) {
            throw new ApiException(ResultCode.EXAM_RESULT_ALREADY_SUBMITTED);
        }

        result.setStatus(2); // 已复核
        result.setReviewerId(reviewerId);
        result.setUpdateTime(LocalDateTime.now());
        resultMapper.updateById(result);
    }

    @Override
    public Page<ExamResult> getPendingReviewResults(Integer pageNum, Integer pageSize, Long departmentId) {
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getStatus, 1); // 待复核
        if (departmentId != null) {
            wrapper.eq(ExamResult::getDepartmentId, departmentId);
        }
        wrapper.orderByDesc(ExamResult::getCreateTime);
        return resultMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Page<ExamResult> getAbnormalResults(Integer pageNum, Integer pageSize, Long recordId) {
        LambdaQueryWrapper<ExamResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamResult::getRecordId, recordId)
                .eq(ExamResult::getAbnormal, 1)
                .orderByAsc(ExamResult::getItemId);
        return resultMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
} 