package fluffysnow.idearly.member.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.*;
import fluffysnow.idearly.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        accessTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 액세스 토큰: 7일
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 리프레쉬 토큰: 7일
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
        Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 액세스 토큰: 7일
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 리프레쉬 토큰: 7일
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ApiResponse.ok(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CookieValue("accessToken") String accessToken, @CookieValue("refreshToken") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        memberService.logout(tokenRequestDto);
        Cookie accessTokenCookie = new Cookie("accessToken", "None");
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 액세스 토큰 삭제

        Cookie refreshTokenCookie = new Cookie("refreshToken", "None");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 리프레쉬 토큰 삭제

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ApiResponse.ok(null);
    }

    @PatchMapping("/members")
    public ApiResponse<EditMemberResponseDto> updateMember(@RequestBody EditMemberRequestDto requestDto) {

        Long loginMemberId = getLoginMemberId();

        EditMemberResponseDto responseDto = memberService.editMember(requestDto, loginMemberId);

        return ApiResponse.ok(responseDto);
    }

    @DeleteMapping("/members")
    public ApiResponse<Void> deleteMember(@CookieValue("accessToken") String accessToken, @CookieValue("refreshToken") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        Long loginMemberId = getLoginMemberId();
        memberService.withdrawMember(tokenRequestDto, loginMemberId);

        Cookie accessTokenCookie = new Cookie("accessToken", "None");
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 액세스 토큰 삭제

        Cookie refreshTokenCookie = new Cookie("refreshToken", "None");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 리프레쉬 토큰 삭제

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ApiResponse.ok(null);
    }

    private static Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginMemberId = null;

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            // 인증 정보 사용
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            loginMemberId = customUserDetails.getMemberId();
        }
        return loginMemberId;
    }
}
