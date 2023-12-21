package fluffysnow.idearly.problem.dto.submit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SumbitTestCaseInfo {
    private Long testCaseId;
    private String status;  // pass, failed, error, timeout

    // 정적 팩토리 메서드로 해당 Dto를 생성합니다.
    public static SumbitTestCaseInfo of(Long testCaseId, String status) {
        return new SumbitTestCaseInfo(testCaseId, status);
    }
}