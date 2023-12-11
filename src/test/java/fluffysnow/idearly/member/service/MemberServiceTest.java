package fluffysnow.idearly.member.service;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void createUser() {

        MemberDto dto = MemberDto.builder()
                .email("aaa@naver.com")
                .name("Gwan")
                .password("123")
                .build();

        Member member = memberService.createUser(dto);

        System.out.println(member.getPassword());

        assertNotNull(member);
        assertEquals(dto.getName(), member.getName());
        assertEquals(dto.getEmail(), member.getEmail());
        assertEquals(member.getRole().toString(), "USER");
    }
}