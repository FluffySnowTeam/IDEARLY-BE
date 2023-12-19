package fluffysnow.idearly.team.dto;

import fluffysnow.idearly.competition.dto.TeammateResponseDto;
import fluffysnow.idearly.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TeamDetailResponseDto {

    private Long teamId;
    private String teamName;
    private Long competitionId;
    private String competitionTitle;
    private String leaderName;
    private String leaderEmail;
    private List<TeammateResponseDto> teammates;

    public static TeamDetailResponseDto from(Team team) {

        return new TeamDetailResponseDto(
                team.getId(),
                team.getName(),
                team.getCompetition().getId(),
                team.getCompetition().getName(),
                team.getLeader().getName(),
                team.getLeader().getEmail(),
                TeammateResponseDto.listFrom(team.getMemberTeamList())
        );
    }

}
