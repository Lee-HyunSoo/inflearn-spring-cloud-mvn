package com.example.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class Greeting {

    /* application.yml 에 존재하는 값을 가져올 수 있는 @Value */
    @Value("${greeting.message}")
    private String message;
}
