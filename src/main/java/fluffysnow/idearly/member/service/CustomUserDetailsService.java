package fluffysnow.idearly.member.service;

import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberRequestDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
        return new CustomUserDetails(new MemberRequestDto(member.getId(), member.getEmail(), member.getName(), member.getPassword(), member.getRole()));
    }
}
