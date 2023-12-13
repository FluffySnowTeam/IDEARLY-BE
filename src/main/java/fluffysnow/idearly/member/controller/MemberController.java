package fluffysnow.idearly.member.controller;

import fluffysnow.idearly.member.dto.LoginRequestDto;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import fluffysnow.idearly.member.dto.TokenDto;
import fluffysnow.idearly.member.dto.TokenRequestDto;
import fluffysnow.idearly.member.service.MemberService;
import jakarta.servlet.http.Cookie;
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
    public void createUser(@RequestBody MemberCreateRequestDto dto) {
        memberService.createUser(dto);
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        TokenDto tokenDto = memberService.login(loginRequestDto);
        Cookie cookie = new Cookie("accessToken", tokenDto.getAccessToken());
        cookie.setPath("/**");
        cookie.setMaxAge(1000 * 60 * 30); // 액세스 토큰: 30분
        Cookie c = new Cookie("refreshToken", tokenDto.getRefreshToken());
        c.setPath("/**");
        c.setMaxAge(1000 * 60 * 60 * 2); // 리프레쉬 토큰: 2시간
        response.addCookie(cookie);
        response.addCookie(c);
    }

    @PostMapping("/reissue")
    public String reissue(@CookieValue String accessToken, @CookieValue String refreshToken) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        return memberService.reissue(tokenRequestDto);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue String accessToken, @CookieValue String refreshToken) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        memberService.logout(tokenRequestDto);
    }


}
