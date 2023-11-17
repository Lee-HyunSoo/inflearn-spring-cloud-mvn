package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /* 회원가입 */
    UserDto createUser(UserDto userDto);
    /* 단일 유저 조회 */
    UserDto getUserByUserId(String userId);
    /* 전체 유저 조회 */
    Iterable<UserEntity> getUserByAll();
    /* 이메일(ID)를 통해 유저 조회 */
    UserDto getUserDetailsByEmail(String email);
}
