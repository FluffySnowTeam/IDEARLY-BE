package fluffysnow.idearly.member.service;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Member createUser(MemberCreateRequestDto dto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(dto.getEmail());
        if (memberOptional.isPresent()) {
            log.info("이미 존재하는 이메일입니다.");
        }
        Member encoding = new Member(dto.getEmail(), dto.getName(), bCryptPasswordEncoder.encode(dto.getPassword()), Role.USER);

        return memberRepository.save(encoding);
    }

}
