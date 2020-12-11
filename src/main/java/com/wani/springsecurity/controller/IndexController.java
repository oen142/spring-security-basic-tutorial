package com.wani.springsecurity.controller;

import com.wani.springsecurity.config.auth.PrincipalDetails;
import com.wani.springsecurity.domain.User;
import com.wani.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody
    String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails) { // DI(의존성 주입)
        System.out.println("=============================");
        PrincipalDetails principalDetail = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetail.getUser());
        System.out.println("userDetails : " + principalDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody
    String testOauthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) { // DI(의존성 주입)
        System.out.println("/test/oauth/login=============================/");
        OAuth2User principalDetail = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetail.getAttributes());
        System.out.println("oauth2User : " + oAuth2User.getAttributes());
        return "세션 정보 확인하기";
    }

    /*
    * 스프링 시큐리티는 시큐리티세션을 갖고있다.
    * 일반적으로는 웹서버가 세션을 가지지만
    * 시큐리티 세션은 따로 갖는다.
    * 시큐리티 세션이 가질수 있는 세션은 Authentication 객체밖에 가질수 없다.
    * 필요할때마다 Controller에서 DI를 할수 있다.
    * Authentication 안에는 UserDetails 타입 , OAuth2User타입이 들어갈 수 있다.
    * UserDetails는 일반로그인할때 들어간다.
    * OAuth2User Oauth로그인할때 들어간다.
    *
    * 로그인할때 꺼내 써야하는데 불편한게 있다.
    * X라는 클래스를 만들어서 UserDetails를 상속받고 OAuth2User를 impl하면
    * Authentication UserDetails, OAuth2User 둘중 하나면 로그인이 가능하다.
    * */
    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encodePassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodePassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody
    String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody
    String data() {
        return "데이터 정보";
    }
}
