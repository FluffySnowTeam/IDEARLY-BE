package fluffysnow.idearly.member.service;

import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.*;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
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
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("회원 정보를 찾을 수 없습니다."));

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
        String email = "aaa@naver.com";

        MemberDuplicateCheckResponseDto memberDuplicateCheckResponseDto = memberService.duplicateCheck(email);

        assertTrue(memberDuplicateCheckResponseDto.isDuplicate());
    }

    @Test
    @DisplayName("회원정보 수정 체크")
    void editMemberTest() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto("bbb@naver.com", "정관휘", "12345678");
        memberService.createUser(dto);
        Member member = memberRepository.findByEmail("bbb@naver.com").orElseThrow(() -> new NotFoundException("회원정보가 없습니다."));
        EditMemberRequestDto editMemberRequestDto = new EditMemberRequestDto("사용자");

        EditMemberResponseDto responseDto = memberService.editMember(editMemberRequestDto, member.getId());

        assertEquals(responseDto.getEmail(), "bbb@naver.com");
        assertEquals(responseDto.getName(), "사용자");
    }
}