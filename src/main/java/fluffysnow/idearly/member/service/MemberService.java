package fluffysnow.idearly.member.service;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.common.exception.BadRequestException;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.common.exception.UnauthorizedException;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.config.jwt.JwtProvider;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.*;
import fluffysnow.idearly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public SignupResponseDto createUser(MemberCreateRequestDto dto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(dto.getEmail());
        if (memberOptional.isPresent()) {
            log.info("이미 존재하는 이메일입니다."); // 에러 처리
            throw new BadRequestException("이미 존재하는 회원의 회원가입 요청입니다.");
        }
        Member encoding = new Member(dto.getEmail(), dto.getName(), bCryptPasswordEncoder.encode(dto.getPassword()), Role.USER);

        memberRepository.save(encoding);
        return SignupResponseDto.of(encoding);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthentication();

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        TokenDto tokenDto = jwtProvider.createTokenDto(authentication);

        redisTemplate.opsForValue().set("RT:" + authentication.getName(), tokenDto.getRefreshToken(),
                tokenDto.getRefreshTokenExpiresIn(),
                TimeUnit.MILLISECONDS);

        log.info("로그인 완료");
        return LoginResponseDto.of(principal, tokenDto);
    }

    public TokenDto reissue(TokenRequestDto dto) {
        if (!jwtProvider.validateToken(dto.getRefreshToken())) {
            log.info("FAIL"); //에러 처리
        }

        Authentication authentication = jwtProvider.getAuthentication(dto.getAccessToken());

        String refreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());

        log.info("RefreshToken: {}", refreshToken);

        if (ObjectUtils.isEmpty(refreshToken)) {
            log.info("잘못된 요청입니다."); //에러 처리
            throw new UnauthorizedException("인증되지 않은 사용자의 요청입니다.");
        }

        if (!refreshToken.equals(dto.getRefreshToken())) {
            log.info("Refresh Token 정보 불일치"); //에러 처리
            throw new UnauthorizedException("인증되지 않은 사용자의 요청입니다.");
        }

        TokenDto tokenDto = jwtProvider.createTokenDto(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenDto.getRefreshToken(),
                        tokenDto.getRefreshTokenExpiresIn(), TimeUnit.MILLISECONDS);

        return tokenDto;
    }

    public void logout(TokenRequestDto dto) {
        if (!jwtProvider.validateToken(dto.getAccessToken())) {
            log.info("잘못된 요청입니다."); //에러 처리
            throw new UnauthorizedException("인증되지 않은 사용자의 요청입니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(dto.getAccessToken());

        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            //refresh token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        Long expiration = jwtProvider.getExpiration(dto.getAccessToken());
        redisTemplate.opsForValue()
                .set(dto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public MemberDuplicateCheckResponseDto duplicateCheck(String email) {
        boolean duplicate = memberRepository.findByEmail(email).isPresent();
        return MemberDuplicateCheckResponseDto.from(duplicate);
    }

    @Transactional
    public EditMemberResponseDto editMember(EditMemberRequestDto editMemberRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("회원정보를 찾을 수 없습니다."));

        member.update(editMemberRequestDto.getName());

        return EditMemberResponseDto.of(member);
    }

    @Transactional
    public void withdrawMember(TokenRequestDto requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("회원정보를 찾을 수 없습니다."));

        memberRepository.delete(member);
        logout(requestDto);
    }
}
