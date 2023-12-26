package fluffysnow.idearly.member.dto;

import fluffysnow.idearly.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EditMemberResponseDto {

    private String email;

    private String name;

    public static EditMemberResponseDto of(Member member) {
        return new EditMemberResponseDto(
                member.getEmail(),
                member.getName()
        );
    }
}
