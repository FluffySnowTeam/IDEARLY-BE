package fluffysnow.idearly.textchat.interceptor;


import fluffysnow.idearly.textchat.common.TextChatContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

import static fluffysnow.idearly.textchat.common.TextChatConst.MY_SESSION_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final TextChatContextHolder contextHolder;

    /**
     * 웹소켓 연결 해제 요청시에
     * contextHolder에서 memberId를 삭제해 주어야 함
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = getSessionId(header);
        log.info("커넥션 종료 요청. Session ID: {}", sessionId);
        contextHolder.removeMember(sessionId);
    }

    /**
     * StompHeaderAccessor로부터 현재 연결된 세션 ID를 가져오는 기능
     */
    private String getSessionId(StompHeaderAccessor header) {
        Map<String, Object> sessionAttributes = header.getSessionAttributes();
        return sessionAttributes.get(MY_SESSION_ID).toString();
    }
}
