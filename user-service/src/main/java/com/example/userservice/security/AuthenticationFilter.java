package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment env;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.env = env;
        this.userService = userService;
    }

    /**
     * 요청 정보를 보낼 시 처리하는 메서드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        /* 클라이언트 요청 정보 전환 */
        /* 전달 된 inputStream에 들어있는 값을 java class 타입으로 변경 */
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            /* 인증 정보 생성 */
            /* 인증 정보를 생성 하기 위해 UsernamePasswordAuthenticationFilter 에 값을 전달 해줘야 한다. */
            /* 이를 위해 전달 된 값으로 SpringSecurity Authentication Package 내 UsernamePasswordAuthenticationToken 생성 */
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>());

            /* 이 후, Token 값을 처리하기 위해 AuthenticationManager 에 인증 작업을 요청 */
            /* 즉, 값을 넘기면 AuthenticationManager 가 ID와 PW를 비교 해준다. */
            return getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 성공 시 정확히 어떤 처리를 해줄지 정의
     * (토큰을 만들 때 만료 시간이 언제 일지, 로그인 성공 시 반환 값으로 무엇을 줄 것 인지 등)
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        /* 사용자의 username 추출 */
        String username = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(username);

        /* JWT 생성 */
        String token = Jwts.builder()
                /* token subject */
                .setSubject(userDetails.getUserId())
                /* token 유효 기간 */
                /* 현재 시간 + 하루 */
                /* application.yml 에서 가져오는 데이터는 모두 String 이라서, 숫자 형태로 변환하여 더한다.  */
                .setExpiration(new Date(
                        System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                /* 암호화를 위한 알고리즘 */
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        /* 생성한 토큰, userId 를 ResponseHeader 에 등록 */
        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
    }
}
