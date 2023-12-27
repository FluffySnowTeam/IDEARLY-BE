package fluffysnow.idearly.config.jwt;

import fluffysnow.idearly.common.exception.UnauthorizedException;
import fluffysnow.idearly.member.dto.TokenDto;
import fluffysnow.idearly.member.dto.TokenRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String cookieAccessToken = resolveToken(request, "accessToken");

        Authentication jwtProviderAuthentication = jwtProvider.getAuthentication(cookieAccessToken);

        String refreshToken = redisTemplate.opsForValue().get("RT:" + jwtProviderAuthentication.getName());

        String cookieRefreshToken = resolveToken(request, "refreshToken");

        boolean accessTokenIsExpired = jwtProvider.validateAccessToken(cookieAccessToken);

        if (StringUtils.hasText(refreshToken) && accessTokenIsExpired) {

            if (cookieRefreshToken.equals(refreshToken)) {

                log.info("리이슈 처리중");

                TokenRequestDto tokenRequestDto = new TokenRequestDto(cookieAccessToken, refreshToken);

                TokenDto reissueToken = reissue(tokenRequestDto);

                setReissueCookie(reissueToken, request, response);

                cookieAccessToken = reissueToken.getAccessToken();
            }
        }

        log.info("jwt 토큰 = {}   {}", cookieAccessToken, request.getRequestURI());

        if (StringUtils.hasText(cookieAccessToken) && jwtProvider.validateToken(cookieAccessToken)) {
            /*1. Redis 에 해당 accessToken logout 여부 확인 */
            String isLogout = redisTemplate.opsForValue().get(cookieAccessToken);
            if (ObjectUtils.isEmpty(isLogout)) {
                log.info("avcavavavavavavavavavavavavavava");
                /*2. 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장 */
                Authentication authentication = jwtProvider.getAuthentication(cookieAccessToken);
                log.info("cdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcd");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookieName.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }

    private void setReissueCookie(TokenDto tokenDto, HttpServletRequest request, HttpServletResponse response) {

        Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.getAccessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60 * 3); // 액세스 토큰: 3시간
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 3); // 리프레쉬 토큰: 3시간
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    private TokenDto reissue(TokenRequestDto dto) {
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

        log.info("리이슈 완료");

        return tokenDto;
    }
}
