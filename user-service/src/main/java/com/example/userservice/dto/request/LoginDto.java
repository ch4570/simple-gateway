package com.example.userservice.dto.request;

import lombok.Data;

@Data
public class LoginDto {

    private String email;
    private String pwd;
}
