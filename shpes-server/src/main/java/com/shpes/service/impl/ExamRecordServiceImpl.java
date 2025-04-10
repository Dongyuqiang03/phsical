package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.enums.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamRecord;
import com.shpes.entity.SysUser;
import com.shpes.mapper.ExamAppointmentMapper;
import com.shpes.mapper.ExamRecordMapper;
import com.shpes.service.ExamRecordService;
import com.shpes.service.SysUserService;
import com.shpes.vo.ExamRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private ExamAppointmentMapper appointmentMapper;

    @Override
    public CommonPage<ExamRecordVO> getRecordPage(Integer pageNum, Integer pageSize, Long userId, 
            Integer status, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        if (startDate != null) {
            wrapper.ge(ExamRecord::getExamDate, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(ExamRecord::getExamDate, endDate.plusDays(1).atStartOfDay());
        }
        if (status != null) {
            wrapper.eq(ExamRecord::getStatus, status);
        }
        wrapper.orderByDesc(ExamRecord::getCreateTime);

        Page<ExamRecord> page = page(new Page<>(pageNum, pageSize), wrapper);
        List<ExamRecordVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public ExamRecordVO getRecordById(Long id) {
        ExamRecord record = getById(id);
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        return convertToVO(record);
    }

    @Override
    public List<ExamRecordVO> getUserRecords(Long userId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreateTime);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordVO createRecord(ExamRecord record) {
        record.setStatus(1); // 设置初始状态为进行中
        record.setExamNo(generateExamNo());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        save(record);
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordVO updateRecord(ExamRecord record) {
        if (!updateById(record)) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long id) {
        if (!removeById(id)) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordVO updateStatus(Long id, Integer status) {
        ExamRecord record = getById(id);
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        record.setStatus(status);
        record.setUpdateTime(LocalDateTime.now());
        updateById(record);
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordVO completeRecord(Long id, String conclusion, String suggestion) {
        ExamRecord record = getById(id);
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        record.setStatus(2); // 设置状态为已完成
        record.setConclusion(conclusion); // 使用 conclusion 字段替代 summary
        record.setSuggestion(suggestion);
        record.setCompleteTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        updateById(record);
        return convertToVO(record);
    }

    @Override
    public Object getTodayStats() {
        return baseMapper.selectTodayStats();
    }

    @Override
    public List<Map<String, Object>> getCompletionStats(LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectCompletionStats(startDate, endDate);
    }

    /**
     * 生成体检编号
     */
    private String generateExamNo() {
        return "EX" + System.currentTimeMillis();
    }

    /**
     * 将实体转换为VO
     */
    private ExamRecordVO convertToVO(ExamRecord record) {
        ExamRecordVO vo = new ExamRecordVO();
        BeanUtils.copyProperties(record, vo);
        
        // 查询并添加用户详细信息
        if (record.getUserId() != null) {
            SysUser user = userService.getById(record.getUserId());
            if (user != null) {
                vo.setUserName(user.getRealName());
                vo.setGender(user.getGender());
                vo.setPhone(user.getPhone());
            }
        }
        
        // 获取预约编号
        if (record.getAppointmentId() != null) {
            try {
                ExamAppointment appointment = appointmentMapper.selectById(record.getAppointmentId());
                if (appointment != null) {
                    vo.setAppointmentNo(appointment.getAppointmentNo());
                }
            } catch (Exception e) {
                log.error("获取预约编号失败，预约ID: {}", record.getAppointmentId(), e);
            }
        }
        
        // 其他需要处理的关联数据...
        
        return vo;
    }
}