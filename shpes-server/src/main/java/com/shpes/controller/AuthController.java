package com.shpes.controller;

import com.shpes.common.api.CommonResult;
import com.shpes.utils.CaptchaUtils;
import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.service.AuthService;
import com.shpes.utils.SecurityUtils;
import com.shpes.vo.CaptchaVO;
import com.shpes.vo.LoginVO;
import com.shpes.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 认证控制器
 * 极简版本
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private CaptchaUtils captchaUtils;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 验证验证码
        if (!captchaUtils.verify(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode())) {
            return CommonResult.failed("验证码错误或已过期");
        }
        return CommonResult.success(authService.login(loginDTO));
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public CommonResult<Void> logout() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            authService.logout(userId);
        } finally {
            SecurityContextHolder.clearContext();
        }
        return CommonResult.success(null);
    }

    @ApiOperation("重置密码")
    @PostMapping("/password/reset")
    public CommonResult<Void> resetPassword(
            @ApiParam("用户名") 
            @NotBlank(message = "用户名不能为空") 
            @RequestParam String username) {
        authService.resetPassword(username);
        return CommonResult.success(null);
    }

    @ApiOperation("修改密码")
    @PostMapping("/password/change")
    public CommonResult<Void> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.updatePassword(userId, passwordDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public CommonResult<CaptchaVO> getCaptcha() {
        return CommonResult.success(captchaUtils.generateCaptcha());
    }

    @ApiOperation("刷新Token")
    @PostMapping("/token/refresh")
    public CommonResult<LoginVO> refreshToken() {
        Long userId = SecurityUtils.getCurrentUserId();
        return CommonResult.success(authService.refreshToken(userId));
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public CommonResult<UserVO> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return CommonResult.success(authService.getCurrentUser(userId));
    }
}
