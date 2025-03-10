package com.shpes.controller;

import com.shpes.common.Result;
import com.shpes.entity.LoginDTO;
import com.shpes.entity.PasswordDTO;
import com.shpes.entity.SysUser;
import com.shpes.service.AuthService;
import com.shpes.utils.CaptchaUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "认证接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaUtils captchaUtils;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        // 验证验证码
        if (!captchaUtils.verify(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode())) {
            return Result.error("验证码错误");
        }
        return authService.login(loginDTO);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return authService.logout(token);
    }

    @ApiOperation("重置密码")
    @PostMapping("/password/reset")
    public Result<?> resetPassword(@RequestBody @Valid PasswordDTO passwordDTO) {
        return authService.resetPassword(passwordDTO);
    }

    @ApiOperation("修改密码")
    @PostMapping("/password/change")
    public Result<?> changePassword(@RequestBody @Valid PasswordDTO passwordDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return authService.changePassword(passwordDTO, token);
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public Result<?> getCaptcha() {
        return Result.success(captchaUtils.generate());
    }
}