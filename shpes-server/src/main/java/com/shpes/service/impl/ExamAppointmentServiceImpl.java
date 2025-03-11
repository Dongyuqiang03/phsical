package com.shpes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shpes.common.api.CommonPage;
import com.shpes.common.api.ResultCode;
import com.shpes.common.exception.ApiException;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamPackage;
import com.shpes.entity.SysUser;
import com.shpes.mapper.ExamAppointmentMapper;
import com.shpes.mapper.ExamPackageMapper;
import com.shpes.mapper.SysUserMapper;
import com.shpes.service.ExamAppointmentService;
import com.shpes.vo.AppointmentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 体检预约服务实现类
 */
@Service
public class ExamAppointmentServiceImpl extends ServiceImpl<ExamAppointmentMapper, ExamAppointment> implements ExamAppointmentService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ExamPackageMapper packageMapper;

    @Override
    public CommonPage<AppointmentVO> getAppointmentPage(Integer pageNum, Integer pageSize, Long userId,
            LocalDate startDate, LocalDate endDate, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<ExamAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, ExamAppointment::getUserId, userId)
                .ge(startDate != null, ExamAppointment::getAppointmentTime, startDate)
                .le(endDate != null, ExamAppointment::getAppointmentTime, endDate)
                .eq(status != null, ExamAppointment::getStatus, status)
                .orderByDesc(ExamAppointment::getCreateTime);

        // 执行分页查询
        Page<ExamAppointment> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectPage(page, wrapper);

        // 转换记录列表
        List<AppointmentVO> records = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 返回通用分页对象
        return CommonPage.restPage(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public AppointmentVO getAppointment(Long id) {
        ExamAppointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_FOUND);
        }
        return convertToVO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAppointment(ExamAppointment appointment) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(appointment.getUserId());
        if (user == null) {
            throw new ApiException(ResultCode.USER_NOT_FOUND);
        }

        // 检查套餐是否存在
        ExamPackage examPackage = packageMapper.selectById(appointment.getPackageId());
        if (examPackage == null) {
            throw new ApiException(ResultCode.PACKAGE_NOT_FOUND);
        }

        // 设置初始状态
        appointment.setStatus(0); // 待体检
        baseMapper.insert(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long id, String reason) {
        ExamAppointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_FOUND);
        }

        if (appointment.getStatus() != 0) {
            throw new ApiException(ResultCode.APPOINTMENT_CANNOT_CANCEL);
        }

        appointment.setStatus(2); // 已取消
        appointment.setCancelReason(reason);
        baseMapper.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAppointmentTime(Long id, Long timeSlotId) {
        ExamAppointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_FOUND);
        }

        if (appointment.getStatus() != 0) {
            throw new ApiException(ResultCode.APPOINTMENT_CANNOT_UPDATE);
        }

        appointment.setTimeSlotId(timeSlotId);
        baseMapper.updateById(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAppointment(Long id) {
        ExamAppointment appointment = baseMapper.selectById(id);
        if (appointment == null) {
            throw new ApiException(ResultCode.APPOINTMENT_NOT_FOUND);
        }

        if (appointment.getStatus() != 0) {
            throw new ApiException(ResultCode.APPOINTMENT_CANNOT_COMPLETE);
        }

        appointment.setStatus(1); // 已完成
        baseMapper.updateById(appointment);
    }

    /**
     * 将实体对象转换为VO对象
     */
    private AppointmentVO convertToVO(ExamAppointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);

        // 获取用户信息
        SysUser user = userMapper.selectById(appointment.getUserId());
        if (user != null) {
            vo.setUserName(user.getName());
        }

        // 获取套餐信息
        ExamPackage examPackage = packageMapper.selectById(appointment.getPackageId());
        if (examPackage != null) {
            vo.setPackageName(examPackage.getName());
        }

        return vo;
    }
} 