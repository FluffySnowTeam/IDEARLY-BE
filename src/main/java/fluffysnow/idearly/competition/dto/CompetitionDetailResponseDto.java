package fluffysnow.idearly.competition.dto;


import fluffysnow.idearly.common.InviteStatus;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionDetailResponseDto {

    private Long competitionId;
    private String competitionTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;

    // 회원이 로그인한 상태인지를 전달
    private boolean login;

    // 회원이 해당 대회에서 소속된 팀을 확인
    private boolean participate;
    private Long teamId;
    private String teamName;

    public static CompetitionDetailResponseDto of(Competition competition, boolean login, MemberTeam memberTeam) {

        if (memberTeam == null) {
            return new CompetitionDetailResponseDto(
                    competition.getId(),
                    competition.getName(),
                    competition.getStartDatetime(),
                    competition.getEndDatetime(),
                    competition.getDescription(),
                    login,
                    false,
                    null,
                    null
            );
        } else {
            // 수락된 상태라면 acceptInvitation은 true
            // 초대만 받은 상태라면 acceptInvitation은 false이고 memberTeam.getTeam().getId()/memberTeam.getTeam().getName()만 나옴
            boolean acceptInvitation = InviteStatus.ACCEPT.equals(memberTeam.getInviteStatus());
            return new CompetitionDetailResponseDto(
                    competition.getId(),
                    competition.getName(),
                    competition.getStartDatetime(),
                    competition.getEndDatetime(),
                    competition.getDescription(),
                    login,
                    acceptInvitation,
                    memberTeam.getTeam().getId(),
                    memberTeam.getTeam().getName()
            );
        }
    }
}
