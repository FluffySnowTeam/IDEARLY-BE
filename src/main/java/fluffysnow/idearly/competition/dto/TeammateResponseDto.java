package fluffysnow.idearly.competition.dto;


import fluffysnow.idearly.common.InviteStatus;
import fluffysnow.idearly.team.domain.MemberTeam;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TeammateResponseDto {

    private String name;
    private String email;
    private InviteStatus inviteStatus;

    public static TeammateResponseDto from(MemberTeam memberTeam) {
        return new TeammateResponseDto(
                memberTeam.getMember().getName(),
                memberTeam.getMember().getEmail(),
                memberTeam.getInviteStatus()
        );
    }

    public static List<TeammateResponseDto> listFrom(List<MemberTeam> memberTeamList) {
        return memberTeamList.stream().map(TeammateResponseDto::from).toList();
    }
}
