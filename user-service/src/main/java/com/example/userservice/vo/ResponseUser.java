package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
/* 반환 시 null 값 방지를 위해 non null option 추가 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {

    private String email;
    private String name;
    private String userId;
    private List<ResponseOrder> orders;
}
