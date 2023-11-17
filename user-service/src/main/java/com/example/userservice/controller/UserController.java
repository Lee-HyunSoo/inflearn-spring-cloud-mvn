package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
//@RequestMapping("/user-service")
public class UserController {

    /* application.yml 에 존재하는 값을 가져올 수 있는 Environment 객체 */
    private final Environment env;
    /* @Value 가 작성되어 있는 Greeting instance */
    private final Greeting greeting;
    private final UserService userService;

    @Timed(value = "users.status", longTask = true)
    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in User Service " +
                ", port(local.server.port) = " + env.getProperty("local.server.port") +
                ", port(server.port) = " +  env.getProperty("serveår.port") +
                ", token secret : = " + env.getProperty("token.secret") +
                ", token expiration time = " + env.getProperty("token.expiration_time")
        );
    }

    @Timed(value = "users.welcome", longTask = true)
    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
//        return env.getProperty("greeting.message");
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
        /* set ModelMapper */
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /* RequestUser -> UserDto */
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.createUser(userDto);

        /* UserDto -> ResponseUser */
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> users = userService.getUserByAll();

        /* UserEntity List를 변환해 담기위한 List 생성 */
        List<ResponseUser> result = new ArrayList<>();
        /* UserEntity List -> ResponseUser List */
        users.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        /* UserDto -> ResponseUser */
        ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
