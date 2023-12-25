package fluffysnow.idearly.textchat.interceptor;

import fluffysnow.idearly.common.exception.ForbiddenException;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fluffysnow.idearly.textchat.common.TextChatConst.MY_SESSION_ID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChannelInboundInterceptor implements ChannelInterceptor {

    private final TextChatContextHolder contextHolder;
    private final MemberTeamRepository memberTeamRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor header = StompHeaderAccessor.wrap(message);
        String sessionId = getSessionId(header);
        Member member = contextHolder.getMember(sessionId);

        // 적절한 인가 권한을 가진 멤버의 요청인지를 검증
        verifySubscribeMessage(header, sessionId, member);
        verifySendMessage(header, sessionId, member);

        return message;
    }

    /**
     * StompHeaderAccessor로부터 현재 연결된 세션 ID를 가져오는 기능
     */
    private String getSessionId(StompHeaderAccessor header) {
        Map<String, Object> sessionAttributes = header.getSessionAttributes();
        return sessionAttributes.get(MY_SESSION_ID).toString();
    }

    private void verifySubscribeMessage(StompHeaderAccessor header, String sessionId, Member member) {
        if (StompCommand.SUBSCRIBE.equals(header.getCommand())) {
            // 구독 요청시에 적절한 인가권한을 가진자의 구독인지 검증
            log.info("command: {}", header.getCommand());   //command: SUBSCRIBE
            log.info("destination: {}", header.getDestination());   //destination: /topic/teams/{teamId}
            log.info("SessionId: {}", sessionId);
            log.info("memberId: {}", member.getId());

            // memberId가 /topic/teams/{teamId}에 구독할 수 있는가?
            // 즉 해당 멤버가 해당 팀에 소속되어 있나?
            // 없다면 예외 던짐
            Long teamId = getTeamIdFromDestination(header.getDestination());
            log.info("teamId: {}", teamId);
//            if (memberTeamRepository.findByMemberIdAndTeamId(member.getId(), teamId).isEmpty()) {
//                throw new ForbiddenException("소속되지 않은 팀으로의 요청이 발생했습니다.");
//            }
        }
    }

    private void verifySendMessage(StompHeaderAccessor header, String sessionId, Member member) {
        if (StompCommand.SEND.equals(header.getCommand())) {
            // 메시지 발행 요청시에 적절한 인가권한을 가진자의 전송인지 검증
            log.info("command: {}", header.getCommand());   //command: SEND
            log.info("destination: {}", header.getDestination());   //destination: /pub/teams/{teamId}
            log.info("SessionId: {}", sessionId);
            log.info("memberId: {}", member.getId());

            // memberId가 /pub/teams/{teamId}에 접근할 수 있는가?
            // 즉 해당 멤버가 해당 팀에 소속되어 있나?
            // 없다면 예외 던짐
            Long teamId = getTeamIdFromDestination(header.getDestination());
            log.info("teamId: {}", teamId);
//            if (memberTeamRepository.findByMemberIdAndTeamId(member.getId(), teamId).isEmpty()) {
//                throw new ForbiddenException("소속되지 않은 팀으로의 요청이 발생했습니다.");
//            }
        }
    }

    private Long getTeamIdFromDestination(String destination) {

        String regex = "/(pub|topic)/teams/([^/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(destination);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(2));
        }
        return null;
    }
}
