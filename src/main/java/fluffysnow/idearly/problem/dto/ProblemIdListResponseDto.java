package fluffysnow.idearly.problem.dto;


import fluffysnow.idearly.problem.domain.Problem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProblemIdListResponseDto {

    private List<Long> problemIdList;

    public static ProblemIdListResponseDto from(List<Problem> problems) {

        return new ProblemIdListResponseDto(problems.stream().map(Problem::getId).toList());

    }
}
