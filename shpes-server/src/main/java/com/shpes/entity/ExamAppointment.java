package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_appointment")
public class ExamAppointment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long packageId;

    private Long timeSlotId;

    private String appointmentNo;

    private Integer status;

    private String cancelReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}