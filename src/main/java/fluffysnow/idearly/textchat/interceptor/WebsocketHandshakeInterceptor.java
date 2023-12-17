package fluffysnow.idearly.textchat.interceptor;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.service.CustomUserDetailsService;
import fluffysnow.idearly.textchat.common.TextChatContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

import static fluffysnow.idearly.textchat.common.TextChatConst.HTTP_ACCESS_TOKEN_NAME;
import static fluffysnow.idearly.textchat.common.TextChatConst.MY_SESSION_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketHandshakeInterceptor implements HandshakeInterceptor {

    private final TextChatContextHolder contextHolder;
    private final CustomUserDetailsService userDetailsService;    //contextHolder 관리에 필요

    /**
     * 웹소켓 연결 요청시에 인증된 사용자인지 확인.
     * sessionId를 하나 생성하여 contextHolder에 멤버 정보와 함께 담아두어, 다른 기능들에서 멤버를 식별할 수 있도록 설정
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 연결 설정 시에
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();

            // 예시
            // 실제로는 JWT TOKEN을 기준으로 Member를 찾아야함
//            Member loginMember = new Member("senderEmail@a.com", "senderName", "asdasd", Role.USER);

            // 실제 동작
            Member loginMember = getLoginMember(servletRequest);

            // 세션을 만들어 현재 회원을 저장
            String sessionId = UUID.randomUUID().toString();
            attributes.put(MY_SESSION_ID, sessionId);
            contextHolder.putMember(sessionId, loginMember);

            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private Member getLoginMember(HttpServletRequest servletRequest) {

        return userDetailsService.findLoginMember();
    }
}
