package fluffysnow.idearly.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String authority;

    public static LoginResponseDto of(TokenDto tokenDto) {
        return new LoginResponseDto(
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(),
                tokenDto.getAuthority()
        );
    }
}
