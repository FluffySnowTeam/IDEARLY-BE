package fluffysnow.idearly.problem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "testcase")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Testcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TINYINT")
    private boolean hidden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    public Testcase(String input, String answer, boolean hidden, Problem problem) {
        this.input = input;
        this.answer = answer;
        this.hidden = hidden;
        this.problem = problem;
    }
}
