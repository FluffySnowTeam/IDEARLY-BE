package fluffysnow.idearly.textchat.common;

import fluffysnow.idearly.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class TextChatContextHolder {

    private static final ConcurrentHashMap<String, Member> memberHolder = new ConcurrentHashMap<>();

    public void putMember(String sessionId, Member member) {
        memberHolder.put(sessionId, member);
    }

    public Member getMember(String sessionId) {
        return memberHolder.get(sessionId);
    }

    public void removeMember(String sessionId) {
        memberHolder.remove(sessionId);
    }

}
