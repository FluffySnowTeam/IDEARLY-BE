package fluffysnow.idearly.admin.dto;

import fluffysnow.idearly.problem.domain.Problem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProblemCreateResponseDto {

    private Long problemId;

    private String name;

    private Long competitionId;

    public static ProblemCreateResponseDto of(Problem problem, Long competitionId) {
        return new ProblemCreateResponseDto(
                problem.getId(),
                problem.getName(),
                competitionId
        );
    }
}
