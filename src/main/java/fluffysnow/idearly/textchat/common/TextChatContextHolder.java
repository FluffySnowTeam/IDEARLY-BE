package fluffysnow.idearly.textchat.common;

import fluffysnow.idearly.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class TextChatContextHolder {

    private final HashMap<String, Member> memberHolder = new HashMap<>();

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
