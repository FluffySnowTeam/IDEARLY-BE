package fluffysnow.idearly.member.service;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.LoginRequestDto;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
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
        Member member = memberService.createUser(dto);
        log.info("password: {}", member.getPassword());

        assertNotNull(member);
        assertEquals(dto.getName(), member.getName());
        assertEquals(dto.getEmail(), member.getEmail());
        assertEquals(member.getRole().toString(), "USER");
    }

    @Test
    void loginTest() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto("aaa@naver.com", "Gwan", "123");

        Member member = memberService.createUser(dto);

        LoginRequestDto loginRequestDto = new LoginRequestDto("aaa@naver.com", "123");

        TokenDto tokenDto = memberService.login(loginRequestDto);
    }
}