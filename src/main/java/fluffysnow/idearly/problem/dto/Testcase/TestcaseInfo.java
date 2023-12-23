package fluffysnow.idearly.problem.dto.Testcase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TestcaseInfo {
    private Long testCaseId;
    private String input;
    private String expectedOutput;
    private String userOutput;
    private String status;  // pass, failed, error, timeout

    // 정적 팩토리 메서드로 해당 Dto를 생성합니다.
    public static TestcaseInfo of(Long testCaseId, String input, String expectedOutput, String userOutput, String status) {
        return new TestcaseInfo(testCaseId, input, expectedOutput, userOutput, status);
    }
}

