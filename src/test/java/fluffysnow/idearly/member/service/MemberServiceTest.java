package fluffysnow.idearly.member.service;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.*;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto("aaa@naver.com", "정관휘", "12345678");
        memberService.createUser(dto);
    }

    @Test
    @DisplayName("회원가입")
    void createUserTest() {
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto("aaa@naver.com", "정관휘", "12345678");
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(); // 오류 출력

        assertNotNull(member);
        assertEquals(member.getName(), memberCreateRequestDto.getName());
        assertEquals(member.getEmail(), memberCreateRequestDto.getEmail());
    }

    @Test
    @DisplayName("로그인")
    void loginTest() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("aaa@naver.com", "12345678");

        LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
    }

    @Test
    @DisplayName("아이디 중복 체크")
    void duplicateCheckTest() {
        MemberDuplicateCheckRequestDto duplicateCheckRequestDto = new MemberDuplicateCheckRequestDto("aaa@naver.com");

        MemberDuplicateCheckResponseDto memberDuplicateCheckResponseDto = memberService.duplicateCheck(duplicateCheckRequestDto);

        assertTrue(memberDuplicateCheckResponseDto.isTf());
    }
}