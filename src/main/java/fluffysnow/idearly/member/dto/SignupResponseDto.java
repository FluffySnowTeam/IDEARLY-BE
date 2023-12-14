package fluffysnow.idearly.member.dto;

import fluffysnow.idearly.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SignupResponseDto {

    private String email;

    private String name;

    public static SignupResponseDto of(Member member) {
        return new SignupResponseDto(
                member.getEmail(),
                member.getName()
        );
    }

}
