package fluffysnow.idearly.problem.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProblemResponseDto {
    private String name;
    private String description;
    private String code;

    // 정적 팩토리 메서드로 해당 Dto를 생성합니다.
    public static ProblemResponseDto of(String name, String description, String code) {
        return new ProblemResponseDto(name, description, code);
    }
}
