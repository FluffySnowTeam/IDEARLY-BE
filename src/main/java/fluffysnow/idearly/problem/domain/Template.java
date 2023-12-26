package fluffysnow.idearly.problem.domain;

import fluffysnow.idearly.common.BaseEntity;
import fluffysnow.idearly.common.Language;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Template extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR")
    private Language language;

    @Column(columnDefinition = "TEXT")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

public Template(Language language, String code, Problem problem) {
        this.language = language;
        this.code = code;
        this.problem = problem;
    }
}
