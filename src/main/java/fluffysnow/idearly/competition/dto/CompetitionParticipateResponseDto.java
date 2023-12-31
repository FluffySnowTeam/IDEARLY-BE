package fluffysnow.idearly.competition.dto;


import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionParticipateResponseDto {

    private Long competitionId;
    private String title;
    private Long teamId;
    private String teamName;
    private List<TeammateResponseDto> teammates;

    public static CompetitionParticipateResponseDto of(Competition competition, Team team) {
        return new CompetitionParticipateResponseDto(
                competition.getId(),
                competition.getName(),
                team.getId(),
                team.getName(),
                team.getMemberTeamList().stream().map(TeammateResponseDto::from).toList()
        );
    }

}
