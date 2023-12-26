package fluffysnow.idearly.admin.dto;

import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberListResponseDto {

    private Long memberId;
    private String name;
    private String email;
    private List<Long> competitionIdList;
    private List<String> competitionTitleList;
    private List<Long> teamIdList;
    private List<String> teamNameList;

    public MemberListResponseDto(Long memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public void setCompetitionAndTeam(List<MemberTeam> memberTeamList) {
        this.competitionIdList = memberTeamList.stream().map(MemberTeam::getCompetition).map(Competition::getId).toList();
        this.competitionTitleList = memberTeamList.stream().map(MemberTeam::getCompetition).map(Competition::getName).toList();
        this.teamIdList = memberTeamList.stream().map(MemberTeam::getTeam).map(Team::getId).toList();
        this.teamNameList = memberTeamList.stream().map(MemberTeam::getTeam).map(Team::getName).toList();
    }

    public static MemberListResponseDto from(Member member) {
        return new MemberListResponseDto(member.getId(), member.getName(), member.getEmail());
    }
}
