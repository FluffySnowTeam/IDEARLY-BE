package fluffysnow.idearly.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fluffysnow.idearly.config.CustomUserDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginResponseDto {

    private Long memberId;
    private String email;
    private String name;
    private String authority;
    @JsonIgnore
    private String accessToken;
    @JsonIgnore
    private String refreshToken;

    public static LoginResponseDto of(CustomUserDetails customUserDetails, TokenDto tokenDto) {
        return new LoginResponseDto(
                customUserDetails.getMemberId(),
                customUserDetails.getUsername(),
                customUserDetails.getName(),
                tokenDto.getAuthority(),
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken()
        );
    }
}
