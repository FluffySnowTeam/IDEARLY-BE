package fluffysnow.idearly.member.service;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void createUser() {

        MemberCreateRequestDto dto = new MemberCreateRequestDto("aaa@naver.com", "Gwan", "123");

        Member member = memberService.createUser(dto);

        System.out.println(member.getPassword());

        assertNotNull(member);
        assertEquals(dto.getName(), member.getName());
        assertEquals(dto.getEmail(), member.getEmail());
        assertEquals(member.getRole().toString(), "USER");
    }
}