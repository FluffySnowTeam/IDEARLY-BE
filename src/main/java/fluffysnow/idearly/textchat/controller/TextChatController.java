package fluffysnow.idearly.textchat.controller;


import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.textchat.common.TextChatConst;
import fluffysnow.idearly.textchat.common.TextChatContextHolder;
import fluffysnow.idearly.textchat.dto.TextChatRequestDto;
import fluffysnow.idearly.textchat.dto.TextChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static fluffysnow.idearly.textchat.common.TextChatConst.*;

@RestController
@RequiredArgsConstructor
public class TextChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TextChatContextHolder textChatContextHolder;

    // /topic/teams/{teamId} 를 구독(subscribe) 신청할 때 발생하는 이벤트 - 입장 알림 등에 활용
//    @SubscribeMapping("/teams/{teamId}")
//    public void enterTextChatRoom(@DestinationVariable("teamId") Long teamId) {
//        simpMessagingTemplate.convertAndSend("/topic/teams/" + teamId, "hello");
//    }


    /**
     * /pub/teams/{teamId} 에 메시지를 발행(publish)할 때의 요청
     * 세션을 이용해 Member를 식별하고,
     * 메시지와 해당 멤버 정보를 이용해 메시지를 전송
     * (인증은 Handshake 시점에 수행하며, 인가 권한은 ChannelInboundInterceptor에서 수행)
     */
    @MessageMapping("/teams/{teamId}")
    public void sendTextChat(TextChatRequestDto message,
                             @DestinationVariable("teamId") Long teamId,
                             StompHeaderAccessor header) {

        String sessionId = getSessionId(header);
        Member loginMember = textChatContextHolder.getMember(sessionId);

        TextChatResponseDto responseDto = TextChatResponseDto.of(message.getChatMessage(), loginMember);

        simpMessagingTemplate.convertAndSend("/topic/teams/" + teamId, responseDto);
    }

    /**
     * StompHeaderAccessor로부터 현재 연결된 세션 ID를 가져오는 기능
     */
    private String getSessionId(StompHeaderAccessor header) {
        Map<String, Object> sessionAttributes = header.getSessionAttributes();
        return sessionAttributes.get(MY_SESSION_ID).toString();
    }
}
