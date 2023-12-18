package fluffysnow.idearly.team.domain;


import fluffysnow.idearly.common.InviteStatus;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(MemberTeamPK.class)
@Table(name = "member_team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberTeam {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private InviteStatus inviteStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    public MemberTeam(Member member, Team team, Competition competition) {
        this.member = member;
        this.team = team;
        this.inviteStatus = InviteStatus.INVITE;
        this.competition = competition;
        team.getMemberTeamList().add(this);
    }

    public void acceptInvitation() {
        this.inviteStatus = InviteStatus.ACCEPT;
    }
}
