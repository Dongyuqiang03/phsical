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
        result.setReviewStatus(1);
        result.setReviewSuggestion(suggestion);
        result.setReviewTime(LocalDateTime.now());
        result.setUpdateTime(LocalDateTime.now());
        updateById(result);
        return convertToVO(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExamResultVO> reviewResults(List<Long> ids) {
        List<ExamResult> results = listByIds(ids);
        LocalDateTime now = LocalDateTime.now();
        results.forEach(result -> {
            result.setReviewStatus(1);
            result.setReviewTime(now);
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