package fluffysnow.idearly.team.domain;

import fluffysnow.idearly.common.BaseEntity;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @OneToMany(mappedBy = "team")
    private List<MemberTeam> memberTeamList;

    public Team(String name, Member leader, Competition competition) {
        this.name = name;
        this.leader = leader;
        this.competition = competition;
    }
}
