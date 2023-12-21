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

    @GetMapping
    public String test() {
        return "ok";
    }

    @GetMapping("/signup/email-check")
    public ApiResponse<MemberDuplicateCheckResponseDto> emailCheck(@RequestParam("email") String email) {
        MemberDuplicateCheckResponseDto responseDto = memberService.duplicateCheck(email);
        return ApiResponse.ok(responseDto);
    }

    @PostMapping("/signup")
    public ApiResponse<SignupResponseDto> createUser(@RequestBody MemberCreateRequestDto dto) {
        SignupResponseDto response = memberService.createUser(dto);
        return ApiResponse.ok(response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
        Cookie accessTokenCookie = new Cookie("accessToken", loginResponseDto.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 3); // 액세스 토큰: 3시간
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 3); // 리프레쉬 토큰: 3시간
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ApiResponse.ok(loginResponseDto);
    }

    @PostMapping("/reissue")
    public ApiResponse<Void> reissue(@CookieValue("accessToken") String accessToken, @CookieValue("refreshToken") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        TokenDto tokenDto = memberService.reissue(tokenRequestDto);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    // 기존 쿠키 수정
                    cookie.setValue(tokenDto.getAccessToken());
                    cookie.setMaxAge(60 * 60 * 3); // 쿠키의 유효 시간 설정 (초 단위)
                    response.addCookie(cookie);
                }
            }
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    // 기존 쿠키 수정
                    cookie.setValue(tokenDto.getRefreshToken());
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60 * 3); // 쿠키의 유효 시간 설정 (초 단위)
                    response.addCookie(cookie);
                }
            }
        }
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CookieValue("accessToken") String accessToken, @CookieValue("refreshToken") String refreshToken) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        memberService.logout(tokenRequestDto);
        return ApiResponse.ok(null);
    }

}
