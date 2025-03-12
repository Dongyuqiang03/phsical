package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "登录响应信息")
public class LoginVO {

    @ApiModelProperty("访问令牌")
    private String token;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色")
    private String role;

    @ApiModelProperty("部门名称")
    private String departmentName;

    public static LoginVO of(String token, String username, String role, String departmentName) {
        return new LoginVO(token, username, role, departmentName);
    }
} 