package fluffysnow.idearly.team.domain;


import fluffysnow.idearly.common.InviteStatus;
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
}
