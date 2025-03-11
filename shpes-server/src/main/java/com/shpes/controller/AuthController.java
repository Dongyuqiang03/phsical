package com.shpes.controller;

import com.shpes.common.api.CommonResult;
import com.shpes.utils.CaptchaUtils;
import com.shpes.dto.LoginDTO;
import com.shpes.dto.PasswordDTO;
import com.shpes.service.AuthService;
import com.shpes.vo.CaptchaVO;
import com.shpes.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
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
            return CommonResult.failed("验证码错误");
        }
        return CommonResult.success(authService.login(loginDTO));
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public CommonResult<Void> logout() {
        authService.logout();
        return CommonResult.success(null);
    }

    @ApiOperation("重置密码")
    @PostMapping("/password/reset")
    public CommonResult<Void> resetPassword(@RequestParam String username) {
        authService.resetPassword(username);
        return CommonResult.success(null);
    }

    @ApiOperation("修改密码")
    @PostMapping("/password/change")
    public CommonResult<Void> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        authService.changePassword(passwordDTO);
        return CommonResult.success(null);
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public CommonResult<CaptchaVO> getCaptcha() {
        return CommonResult.success(captchaUtils.generateCaptcha());
    }
} 