package com.shpes.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.dto.ExamResultUpdateDTO;
import com.shpes.entity.ExamRecord;
import com.shpes.entity.ExamResult;
import com.shpes.mapper.ExamResultMapper;
import com.shpes.service.ExamRecordService;
import com.shpes.service.ExamResultService;
import com.shpes.utils.SecurityUtils;
import com.shpes.vo.ExamRecordVO;
import com.shpes.vo.ExamResultVO;
import com.shpes.dto.ExamResultBatchDTO;
import com.shpes.dto.ExamResultBatchDTO.ExamResultItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * 体检结果服务实现类
 */
@Service
public class ExamResultServiceImpl extends ServiceImpl<ExamResultMapper, ExamResult> implements ExamResultService {

    private static final Logger log = LoggerFactory.getLogger(ExamResultServiceImpl.class);
    
    @Autowired
    private ExamRecordService examRecordService;

    @Override
    public List<ExamResultVO> getResultsByRecordId(Long recordId) {
        // 获取数据库实体对象
        List<ExamResult> results = baseMapper.selectResultsByRecordId(recordId);
        // 转换为VO对象
        return convertToVOList(results);
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
        result.setAnalysis(suggestion);
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
    public List<ExamResultVO> updateResultsFromBatchDTO(ExamResultUpdateDTO batchDTO) {
        List<ExamResult> results = new ArrayList<>();
        
        // 将DTO转换为ExamResult列表并更新
        if (batchDTO.getItems() != null && !batchDTO.getItems().isEmpty()) {
            for (ExamResultUpdateDTO.ExamResultItemUpdateDTO item : batchDTO.getItems()) {
                // 获取现有结果
                ExamResult result = getById(item.getId());
                if (result == null) {
                    throw new ApiException(ResultCode.EXAM_RESULT_NOT_EXIST);
                }
                
                // 更新结果信息
                result.setValue(item.getResult());    // 更新检查结果值
                result.setAbnormal("ABNORMAL".equals(item.getStatus()) ? 1 : 0);  // 更新状态
                result.setAnalysis(item.getAnalysis());  // 更新医生建议/分析
                result.setUpdateTime(LocalDateTime.now());
                
                // 更新到数据库
                updateById(result);
                results.add(result);
            }
        }
        
        // 更新体检记录表中的相关字段
        if (!results.isEmpty() && results.get(0).getRecordId() != null) {
            try {
                Long recordId = results.get(0).getRecordId();
                
                // 获取当前记录
                ExamRecordVO recordVO = examRecordService.getRecordById(recordId);
                if (recordVO != null) {
                    // 创建新的记录对象，设置需要更新的字段
                    ExamRecord updateRecord = new ExamRecord();
                    updateRecord.setId(recordId);
                    updateRecord.setSuggestion(batchDTO.getSuggestion());  // 更新医生建议
                    updateRecord.setConclusion(batchDTO.getConclusion());  // 更新体检结论
                    updateRecord.setMainFindings(batchDTO.getMainFindings());  // 更新主要发现
                    updateRecord.setUpdateTime(LocalDateTime.now());
                    
                    // 更新记录
                    examRecordService.updateRecord(updateRecord);
                    log.info("已更新体检记录相关信息，记录ID: {}", recordId);
                }
            } catch (Exception e) {
                log.error("更新体检记录失败", e);
                throw new ApiException(ResultCode.FAILED);
            }
        }
        
        return convertToVOList(results);
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
                result.setAnalysis(item.getAnalysis());
                
                // 设置操作人员信息（当前登录用户）
                result.setDoctorId(SecurityUtils.getCurrentUserId());
                
                // 设置状态为待复核
                result.setStatus(1);
                
                results.add(result);
            }
        }
        
        // 调用现有方法批量保存结果
        List<ExamResultVO> resultVOs = createResults(results);
        
        // 更新体检记录状态，确保记录被标记为"已录入结果"
        if (!results.isEmpty() && results.get(0).getRecordId() != null) {
            try {
                Long recordId = results.get(0).getRecordId();
                
                // 获取当前记录
                ExamRecordVO recordVO = examRecordService.getRecordById(recordId);
                if (recordVO != null) {
                    // 创建新的记录对象，设置需要更新的字段
                    ExamRecord updateRecord = new ExamRecord();
                    updateRecord.setId(recordId);
                    updateRecord.setStatus(2);  // 更新状态为"进行中(已录入结果)"
                    updateRecord.setSuggestion(batchDTO.getSuggestion());  // 更新医生建议
                    updateRecord.setConclusion(batchDTO.getConclusion());  // 更新体检结论
                    updateRecord.setMainFindings(batchDTO.getMainFindings());  // 更新主要发现
                    updateRecord.setUpdateTime(LocalDateTime.now());
                    
                    // 更新记录
                    examRecordService.updateRecord(updateRecord);
                    log.info("已更新体检记录状态及相关信息，记录ID: {}", recordId);
                }
            } catch (Exception e) {
                log.error("更新体检记录状态失败", e);
                // 不抛出异常，避免影响主要功能
            }
        }
        
        return resultVOs;
    }

    @Override
    public CommonPage<ExamResultVO> getCurrentUserResults(Long userId, Integer pageNum, Integer pageSize,
                                                        Long recordId, String beginDate, String endDate,
                                                        String itemName, Integer resultType) {
        // 使用recordService获取用户的体检记录ID列表
        List<Long> userRecordIds = getUserRecordIds(userId);
        
        if (userRecordIds.isEmpty()) {
            // 用户没有体检记录，返回空结果
            return null;
        }
        
        // 构建查询条件
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("record_id", userRecordIds);
        
        // 添加指定记录ID条件
        if (recordId != null) {
            queryWrapper.eq("record_id", recordId);
        }
        
        // 添加日期范围条件（通过join查询）
        if (StringUtils.hasText(beginDate) || StringUtils.hasText(endDate)) {
            queryWrapper.apply("record_id IN (SELECT id FROM exam_record WHERE 1=1 " +
                (StringUtils.hasText(beginDate) ? "AND exam_date >= '" + beginDate + "'" : "") +
                (StringUtils.hasText(endDate) ? "AND exam_date <= '" + endDate + "'" : "") +
                ")");
        }
        
        // 添加项目名称条件（假设有item_name字段或通过关联查询）
        if (StringUtils.hasText(itemName)) {
            queryWrapper.like("item_name", itemName);
        }
        
        // 添加结果类型条件
        if (resultType != null) {
            queryWrapper.eq("abnormal", resultType);
        }
        
        // 执行分页查询
        Page<ExamResult> page = new Page<>(pageNum, pageSize);
        Page<ExamResult> resultPage = baseMapper.selectPage(page, queryWrapper);
        
        // 转换为VO对象
        List<ExamResultVO> records = convertToVOList(resultPage.getRecords());
        
        // 返回分页结果
        return CommonPage.restPage(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }
    
    /**
     * 获取用户的体检记录ID列表
     */
    private List<Long> getUserRecordIds(Long userId) {
        // 查询用户的所有体检记录
        QueryWrapper<ExamRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .select("id"); // 只查询ID字段
        
        // 使用mapper直接查询，避免循环依赖
        return baseMapper.selectUserRecordIds(userId);
    }

    private ExamResultVO convertToVO(ExamResult result) {
        if (result == null) {
            return null;
        }
        ExamResultVO vo = new ExamResultVO();
        // 手动映射字段，确保与数据库实体字段一致
        vo.setId(result.getId());
        vo.setRecordId(result.getRecordId());
        vo.setItemId(result.getItemId());
        // 将value映射为前端使用的result
        vo.setValue(result.getValue());
        // 将abnormal映射为前端使用的status
        vo.setAbnormal(result.getAbnormal());
        // 将suggestion映射为前端使用的analysis
        vo.setAnalysis(result.getAnalysis());
        vo.setDoctorId(result.getDoctorId());
        vo.setReviewerId(result.getReviewerId());
        vo.setStatus(result.getStatus());
        vo.setRemark(result.getRemark());
        vo.setCreateTime(result.getCreateTime());
        vo.setUpdateTime(result.getUpdateTime());
        return vo;
    }

    private List<ExamResultVO> convertToVOList(List<ExamResult> results) {
        return results.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
}