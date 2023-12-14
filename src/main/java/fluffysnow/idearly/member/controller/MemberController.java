package fluffysnow.idearly.member.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.*;
import fluffysnow.idearly.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<SignupResponseDto> createUser(@RequestBody MemberCreateRequestDto dto) {
        SignupResponseDto response = memberService.createUser(dto);
        return ApiResponse.ok(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
        Cookie cookie = new Cookie("accessToken", loginResponseDto.getAccessToken());
        cookie.setPath("/");
        cookie.setMaxAge(1000 * 60 * 60 * 3); // 액세스 토큰: 3시간
        Cookie c = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        c.setPath("/");
        c.setMaxAge(1000 * 60 * 60 * 3); // 리프레쉬 토큰: 3시간
        response.addCookie(cookie);
        response.addCookie(c);
        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/reissue")
    public ApiResponse<Void> reissue(@CookieValue String accessToken, @CookieValue String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        TokenDto tokenDto = memberService.reissue(tokenRequestDto);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    // 기존 쿠키 수정
                    cookie.setValue(tokenDto.getAccessToken());
                    cookie.setPath("/");
                    cookie.setMaxAge(1000 * 60 * 60 * 3); // 쿠키의 유효 시간 설정 (초 단위)
                    response.addCookie(cookie);
                }
            }
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    // 기존 쿠키 수정
                    cookie.setValue(tokenDto.getRefreshToken());
                    cookie.setPath("/");
                    cookie.setMaxAge(1000 * 60 * 60 * 3); // 쿠키의 유효 시간 설정 (초 단위)
                    response.addCookie(cookie);
                }
            }
        }
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CookieValue String accessToken, @CookieValue String refreshToken) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        memberService.logout(tokenRequestDto);
        return ApiResponse.ok(null);
    }

}
