package fluffysnow.idearly.member.service;

import fluffysnow.idearly.common.exception.UnauthorizedException;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberRequestDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
//        return new CustomUserDetails(new MemberRequestDto(member.getId(), member.getEmail(), member.getName(), member.getPassword(), member.getRole()));
        return new CustomUserDetails(member.getId(), member.getEmail(), member.getName(), member.getPassword(), member.getRole());
    }

    public Member findLoginMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Principal is instanceof {}", authentication.getPrincipal().getClass());

        CustomUserDetails userDetails = null;
        try {
            userDetails = (CustomUserDetails) authentication.getPrincipal();
        } catch (ClassCastException e) {
            throw new UnauthorizedException("인증되지 않은 사용자의 접근입니다.");
        }

        log.info("CustomUserDetails.getMemberId: {}", userDetails.getMemberId());

        return memberRepository.findByEmail(userDetails.getUsername()).orElseThrow();   //userNotFound 예외
    }
}
