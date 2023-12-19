package fluffysnow.idearly.team.dto;


import fluffysnow.idearly.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TeamResponseDto {

    private Long teamId;
    private String teamName;
    private Long competitionId;
    private String competitionTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String leaderName;
    private String leaderEmail;

    private static TeamResponseDto from(Team team) {

        return new TeamResponseDto(
                team.getId(),
                team.getName(),
                team.getCompetition().getId(),
                team.getCompetition().getName(),
                team.getCompetition().getStartDatetime(),
                team.getCompetition().getEndDatetime(),
                team.getLeader().getName(),
                team.getLeader().getEmail()
        );
    }

    public static List<TeamResponseDto> listFrom(List<Team> teams) {
        return teams.stream().map(TeamResponseDto::from).toList();
    }
}
