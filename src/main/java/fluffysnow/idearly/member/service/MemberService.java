package fluffysnow.idearly.member.service;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Member createUser(MemberDto dto) {
        Member encoding = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();
        return memberRepository.save(encoding);
    }

}
