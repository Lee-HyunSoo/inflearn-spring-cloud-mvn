package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@RequiredArgsConstructor
/* @EnableWebSecurity 에 @Configuration 까지 작성하면 다른 bean들 보다 높은 우선순위를 가지게 된다.  */
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    /* 로그인 후 JWT 가 만들어 질 때, 토큰의 유효시간 등의 정보를 가져오기 위해 */
    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 권한 작업을 하기 위한 configure method
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* csrf 설정 미사용 */
        http.csrf().disable();
        /* actuator 를 지나는 모든 uri 허가 -> actuator 사용 가능 */
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        /* 인증이 되어 있는 상태에서만 요청 처리를 하기 위해 모든 요청 제한 */
        http.authorizeRequests().antMatchers("/**")
                /* 해당 IP를 통과 하는 요청만 인증 성공 */
                .hasIpAddress("192.168.0.14")
//                .hasIpAddress("172.20.10.14")
                .and()
                /* Filter set */
                .addFilter(getAuthenticationFilter());

        /* web h2 db는 frame 별로 데이터가 나눠져 있기 때문에, 해당 옵션을 꺼야 시큐리티를 써도 db를 확인할 수 있다. */
        http.headers().frameOptions().disable();
    }

    /**
     * 인증 처리를 하기 위한 configure method
     * 인증이 되어야지만 권한 부여가 가능해진다.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /* 전달 된 username 과 password 를 검색 */
        /* DB 내 Encrypted pw 와 비교하기 위해 로그인 시 입력 된 pw 를 Encrypted */
        /* userDetailsService 의 parameter 는 UserDetailsService 를 상속 받은 클래스여야 한다. */
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * 인증 작업을 진행하기 위해 spring security 에서 가져온 manager 생성
     */
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        return new AuthenticationFilter(authenticationManager(), userService, env);
    }
}
