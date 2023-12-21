package fluffysnow.idearly.problem.dto.submit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SubmitResponseDto {
    private boolean correct;
    private List<TestCaseInfo> testcases;

    // 정적 팩토리 메서드로 해당 Dto를 생성합니다.
    public static SubmitResponseDto of(boolean correct, List<TestCaseInfo> testcases) {
        return new SubmitResponseDto(correct, testcases);
    }
}

