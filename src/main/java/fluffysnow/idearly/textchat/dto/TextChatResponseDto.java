package fluffysnow.idearly.textchat.dto;

import fluffysnow.idearly.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class TextChatResponseDto {

    // 메시지의 고유의 ID?
    private String messageId;
    private String chatMessage;
    private String senderName;
    private String senderEmail;
    private LocalDateTime sendDate;

    public static TextChatResponseDto of(String chatMessage, Member member) {
        UUID messageUUID = UUID.randomUUID();

        return new TextChatResponseDto(
                messageUUID.toString(),
                chatMessage,
                member.getName(),
                member.getEmail(),
                LocalDateTime.now());
    }
}
