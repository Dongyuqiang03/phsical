package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shpes.entity.ExamRecord;
import com.shpes.entity.SysUser;
import com.shpes.service.SysUserService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "体检记录分页列表返回对象")
public class ExamRecordPageVO {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("体检编号")
    private String examNo;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("体检套餐名称")
    private String packageName;

    @ApiModelProperty("体检日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("状态：0-待体检，1-进行中(未录入结果)，2-进行中(已录入结果)，3-已完成")
    private Integer status;

    /**
     * 将实体转换为分页VO对象
     */
    public static ExamRecordPageVO fromEntity(ExamRecord record) {
        if (record == null) {
            return null;
        }
        
        ExamRecordPageVO vo = new ExamRecordPageVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    /**
     * 填充用户信息
     */
    public void fillUserInfo(SysUser user) {
        if (user != null) {
            this.setUserName(user.getRealName());
        }
    }
}