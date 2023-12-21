package fluffysnow.idearly.problem.domain;

import fluffysnow.idearly.common.BaseEntity;
import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Entity
@Table(name = "submit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Submit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submit_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(columnDefinition = "TINYINT")
    private boolean correct;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

}
