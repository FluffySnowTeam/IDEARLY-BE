package fluffysnow.idearly.problem.dto.submit;

import fluffysnow.idearly.common.Language;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SubmitAndTestcaseCreateRequestDto {
    private String code;
    private Language language;

    public static SubmitAndTestcaseCreateRequestDto of(String code, Language language) {
        return new SubmitAndTestcaseCreateRequestDto(code, language);
    }
}
