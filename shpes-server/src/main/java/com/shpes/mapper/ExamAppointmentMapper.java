package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shpes.entity.ExamAppointment;
import com.shpes.entity.ExamAppointmentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 体检预约Mapper接口
 */
@Mapper
public interface ExamAppointmentMapper extends BaseMapper<ExamAppointment> {
    
    /**
     * 获取预约详情
     */
    ExamAppointmentDetail getAppointmentDetail(@Param("id") Long id);
    
    /**
     * 获取用户的预约历史
     */
    List<ExamAppointmentDetail> getUserAppointments(@Param("userId") Long userId);
    
    /**
     * 获取部门某天的预约列表
     */
    List<ExamAppointmentDetail> getDepartmentAppointments(@Param("departmentId") Long departmentId, @Param("date") LocalDate date);
}