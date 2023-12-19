package fluffysnow.idearly.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDuplicateCheckResponseDto {
    private boolean duplicate;

    public static MemberDuplicateCheckResponseDto from(boolean duplicate) {
        return new MemberDuplicateCheckResponseDto(duplicate);
    }
}
