package fluffysnow.idearly.admin.dto;

import fluffysnow.idearly.problem.domain.Testcase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TestcaseCreateResponseDto {

    private Long problemId;

    public static TestcaseCreateResponseDto from(Long problemId) {
        return new TestcaseCreateResponseDto(problemId);
    }
}
