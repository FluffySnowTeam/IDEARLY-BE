package fluffysnow.idearly.admin.dto;

import fluffysnow.idearly.problem.domain.Problem;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProblemsResponseDto {

    private Long id;

    private String name;

    private String description;

    private static ProblemsResponseDto from(Problem problem) {
        return new ProblemsResponseDto(
                problem.getId(),
                problem.getName(),
                problem.getDescription()
        );
    }

    public static List<ProblemsResponseDto> listFrom(List<Problem> problems) {
        return problems.stream().map(ProblemsResponseDto::from).toList();
    }
}
