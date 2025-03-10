package com.shpes.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String username;

    private String role;

    private String departmentName;

    public static LoginResponse of(String token, String username, String role, String departmentName) {
        return new LoginResponse(token, username, role, departmentName);
    }
}