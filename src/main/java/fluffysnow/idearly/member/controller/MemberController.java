package fluffysnow.idearly.member.controller;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public void createUser(@RequestBody MemberDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElse(memberService.createUser(dto));
        if (member != null) {
            log.info("이미 존재하는 이메일입니다.");
        }
    }
}
