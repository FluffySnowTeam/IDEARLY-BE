package fluffysnow.idearly.team.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TeamInviteAcceptResponseDto {
    private Long teamId;
    private boolean accept;

    public static TeamInviteAcceptResponseDto from(Long teamId, boolean accept) {
        return new TeamInviteAcceptResponseDto(teamId, accept);
    }
}
