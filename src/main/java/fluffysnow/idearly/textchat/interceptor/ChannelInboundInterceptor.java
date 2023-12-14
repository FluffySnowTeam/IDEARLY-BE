package fluffysnow.idearly.textchat.interceptor;

import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.textchat.common.TextChatContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static fluffysnow.idearly.textchat.common.TextChatConst.MY_SESSION_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChannelInboundInterceptor implements ChannelInterceptor {

    private final TextChatContextHolder contextHolder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor header = StompHeaderAccessor.wrap(message);
        String sessionId = getSessionId(header);
        Member member = contextHolder.getMember(sessionId);


        if (StompCommand.SUBSCRIBE.equals(header.getCommand())) {
            // 구독 요청시에 적절한 인가권한을 가진자의 구독인지 검증
            log.info("command: {}", header.getCommand());   //command: SUBSCRIBE
            log.info("destination: {}", header.getDestination());   //destination: /topic/teams/{teamId}
            log.info("SessionId: {}", sessionId);
            log.info("memberId: {}", member.getId());

            // memberId가 /topic/teams/{teamId}에 구독할 수 있는가?
            // 즉 해당 멤버가 해당 팀에 소속되어 있나?
            // 없다면 예외 던짐
        }

        if (StompCommand.SEND.equals(header.getCommand())) {
            // 메시지 발행 요청시에 적절한 인가권한을 가진자의 전송인지 검증
            log.info("command: {}", header.getCommand());   //command: SEND
            log.info("destination: {}", header.getDestination());   //destination: /pub/teams/{teamId}
            log.info("SessionId: {}", sessionId);
            log.info("memberId: {}", member.getId());

            // memberId가 /pub/teams/{teamId}에 접근할 수 있는가?
            // 즉 해당 멤버가 해당 팀에 소속되어 있나?
            // 없다면 예외 던짐
        }

        return message;
    }

    /**
     * StompHeaderAccessor로부터 현재 연결된 세션 ID를 가져오는 기능
     */
    private String getSessionId(StompHeaderAccessor header) {
        Map<String, Object> sessionAttributes = header.getSessionAttributes();
        return sessionAttributes.get(MY_SESSION_ID).toString();
    }
}
