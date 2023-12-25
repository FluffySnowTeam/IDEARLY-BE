package fluffysnow.idearly.problem.dto.Testcase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TestcaseResponseDto {
    private boolean correct;
    private List<TestcaseInfo> testcases;

    // 정적 팩토리 메서드로 해당 Dto를 생성합니다.
    public static TestcaseResponseDto of(boolean correct, List<TestcaseInfo> testcases) {
        return new TestcaseResponseDto(correct, testcases);
    }
}
