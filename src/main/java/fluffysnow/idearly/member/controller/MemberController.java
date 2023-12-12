package fluffysnow.idearly.member.controller;

import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
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

    @PostMapping("/signup")
    public void createUser(@RequestBody MemberCreateRequestDto dto) {
        memberService.createUser(dto);
    }
}
