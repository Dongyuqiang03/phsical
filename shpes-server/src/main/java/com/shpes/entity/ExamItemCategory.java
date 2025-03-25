package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_item_category")
public class ExamItemCategory extends BaseEntity {

    @TableField("name")
    private String name;
    
    @TableField("code")
    private String code;
    
    @TableField("status")
    private Integer status;
} 