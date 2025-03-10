package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("exam_time_slot")
public class ExamTimeSlot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deptId;

    private LocalDate examDate;

    private String timePeriod;

    private Integer capacity;

    private Integer booked;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}