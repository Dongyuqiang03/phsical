package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.shpes.mapper.ExamResultMapper;
import com.shpes.service.ExamRecordService;
import com.shpes.service.ExamAppointmentService;
import com.shpes.service.SysUserService;
import com.shpes.vo.ExamRecordDetailVO;
import com.shpes.vo.ExamRecordPageVO;
import com.shpes.vo.ExamRecordVO;
import com.shpes.vo.ExamResultDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Resource
    private SysUserService userService;
    @Resource
    private ExamAppointmentMapper appointmentMapper;
    @Resource
    private ExamResultMapper examResultMapper;
    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Override
    public CommonPage<ExamRecordPageVO> getRecordPage(Integer pageNum, Integer pageSize, Long userId,
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
        // 在 getRecordPage 和其他使用 ExamRecordPageVO 的方法中修改转换逻辑
        List<ExamRecordPageVO> records = page.getRecords().stream()
                .map(record -> {
                    ExamRecordPageVO vo = ExamRecordPageVO.fromEntity(record);
                    if (record.getUserId() != null) {
                        SysUser user = userService.getById(record.getUserId());
                        vo.fillUserInfo(user);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return CommonPage.restPage(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public CommonPage<ExamRecordPageVO> getUserRecords(Long userId, Integer pageNum, Integer pageSize,
                                                      String beginDate, String endDate, String packageName, String status) {
        QueryWrapper<ExamRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (StringUtils.hasText(beginDate) && StringUtils.hasText(endDate)) {
            queryWrapper.between("exam_date", beginDate, endDate);
        } else if (StringUtils.hasText(beginDate)) {
            queryWrapper.ge("exam_date", beginDate);
        } else if (StringUtils.hasText(endDate)) {
            queryWrapper.le("exam_date", endDate);
        }

        if (StringUtils.hasText(packageName)) {
            queryWrapper.like("package_name", packageName);
        }

        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("exam_date");

        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        Page<ExamRecord> resultPage = baseMapper.selectPage(page, queryWrapper);

        List<ExamRecordPageVO> records = resultPage.getRecords().stream()
                .map(ExamRecordPageVO::fromEntity)
                .collect(Collectors.toList());

        return CommonPage.restPage(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecordVO createRecord(ExamRecord record) {
        record.setStatus(1); // 设置初始状态为进行中(未录入结果)
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
    public ExamRecordVO completeRecord(Long id) {
        ExamRecord record = getById(id);
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        record.setStatus(3); // 设置状态为已完成
        record.setCompleteTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        updateById(record);

        // 直接使用Mapper更新预约记录状态为已完成
        if (record.getAppointmentId() != null) {
            ExamAppointment appointment = new ExamAppointment();
            appointment.setId(record.getAppointmentId());
            appointment.setStatus(3); // 设置状态为已完成
            appointment.setUpdateTime(LocalDateTime.now());
            appointmentMapper.updateById(appointment);
        }
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

    @Override
    public List<ExamRecordVO> getUserRecords(Long userId) {
        // 创建查询条件
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getExamDate);

        // 查询结果并转换为VO
        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 生成体检编号
     */
    private String generateExamNo() {
        return "EX" + System.currentTimeMillis();
    }

    @Override
    public ExamRecordDetailVO getRecordDetail(Long id) {
        ExamRecordDetailVO detail = examRecordMapper.getRecordDetail(id);
        if (detail == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }

        // 获取体检结果列表
        List<ExamResultDetailVO> results = examResultMapper.selectResultsByRecordId(id);
        detail.setResults(results);

        return detail;
    }

    @Override
    public ExamRecordDetailVO getRecordDetailByAppointmentId(Long appointmentId) {
        // 根据预约ID查询体检记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getAppointmentId, appointmentId);
        ExamRecord record = getOne(wrapper);
        
        if (record == null) {
            throw new ApiException(ResultCode.EXAM_RECORD_NOT_EXIST);
        }
        
        // 获取体检记录详情
        return getRecordDetail(record.getId());
    }

    private ExamRecordVO convertToVO(ExamRecord record) {
            ExamRecordVO vo = ExamRecordVO.fromEntity(record);
            
            // 查询并添加用户详细信息
            if (record.getUserId() != null) {
                SysUser user = userService.getById(record.getUserId());
                vo.fillUserInfo(user);
            }
    
            // 获取预约编号
            if (record.getAppointmentId() != null) {
                ExamAppointment appointment = appointmentMapper.selectById(record.getAppointmentId());
                vo.fillAppointmentInfo(appointment);
            }
    
            return vo;
        }
}