package fluffysnow.idearly.member.service;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.LoginRequestDto;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import fluffysnow.idearly.member.dto.SignupResponseDto;
import fluffysnow.idearly.member.dto.TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void createUserTest() {

        MemberCreateRequestDto dto = new MemberCreateRequestDto("aaa@naver.com", "Gwan", "123");
        MemberCreateRequestDto memberDto = new MemberCreateRequestDto("aaa@naver.com", "ssss", "123");


        memberService.createUser(memberDto);
        SignupResponseDto response = memberService.createUser(dto);

        assertNotNull(response);
        assertEquals(dto.getName(), response.getName());
        assertEquals(dto.getEmail(), response.getEmail());
    }

    @Test
    void loginTest() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto("aaa@naver.com", "Gwan", "123");

        memberService.createUser(dto);

        LoginRequestDto loginRequestDto = new LoginRequestDto("aaa@naver.com", "123");

        memberService.login(loginRequestDto);
    }
}