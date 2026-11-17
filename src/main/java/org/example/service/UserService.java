package org.example.service;

import org.example.dto.request.LoginRequestDto;
import org.example.dto.request.UserRegisterRequestDto;
import org.example.dto.response.LoginResponseDto;
import org.example.dto.response.UserResponseDto;

public interface UserService {

    UserResponseDto register(UserRegisterRequestDto registerrequestdto);

    LoginResponseDto login(LoginRequestDto loginrequestDto);
}
