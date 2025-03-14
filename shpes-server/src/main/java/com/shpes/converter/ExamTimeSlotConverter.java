package com.shpes.converter;

import com.shpes.entity.ExamTimeSlot;
import com.shpes.vo.ExamTimeSlotVO;
import org.springframework.stereotype.Component;

@Component
public class ExamTimeSlotConverter {

    public ExamTimeSlotVO toVO(ExamTimeSlot entity) {
        if (entity == null) {
            return null;
        }

        ExamTimeSlotVO vo = new ExamTimeSlotVO();
        vo.setId(entity.getId());
        vo.setDepartmentId(entity.getDepartmentId());
        vo.setDate(entity.getDate());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setCapacity(entity.getCapacity());
        vo.setAppointmentCount(entity.getBookedCount());
        vo.setRemainingCapacity(entity.getCapacity() - entity.getBookedCount());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());

        return vo;
    }
}