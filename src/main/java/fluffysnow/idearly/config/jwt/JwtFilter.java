package fluffysnow.idearly.config.jwt;

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

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_ACCESS = "accessToken";

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = resolveToken(request);

        log.info("jwt 토큰 = {}   {}", accessToken, request.getRequestURI());

        if (StringUtils.hasText(accessToken) && jwtProvider.validateToken(accessToken)) {
            /*1. Redis 에 해당 accessToken logout 여부 확인 */
            String isLogout = redisTemplate.opsForValue().get(accessToken);
            if (ObjectUtils.isEmpty(isLogout)) {
                /*2. 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장 */
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> AUTHORIZATION_ACCESS.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue))
                .orElse(null);
    }
}
