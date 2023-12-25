package fluffysnow.idearly.problem.compile;

import fluffysnow.idearly.common.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ExecuteDocker {
    /**
     * 제출된 코드에 대한 실행을 처리하는 메서드
     * @param code: 제출 코드
     * @param input: 하나의 테스트
     * @param language: 제출된 언어
     * @return : 도커 컨테이너에서 실행된 컴파일된 값(결과 값)을 반환 합니다.
     */
    public String executeCode(String code, String input, Language language) {
        DockerConfig dockerConfig = new DockerConfig();
        String imageName = getImageNameForLanguage(language);
        String excutableCode = combineCodeAndInput(code, input, language);

        // 도커 컨테이너 생성 및 실행
        // 도커 컨테이너 실행 및 타임 아웃 처리 로직
        String containerId = dockerConfig.createContainer(excutableCode, imageName, language);
        return executeContainerAndHandleExceptions(dockerConfig, containerId);
    }


    private String executeContainerAndHandleExceptions(DockerConfig dockerConfig, String containerId) {
        try {
            return dockerConfig.executeContainerWithTimeout(containerId, 8, TimeUnit.SECONDS);
        } catch (RuntimeException e) {
            log.error("excuteCode 실행 중 오류 발생 : {}", e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * 제출한 코드의 언어에 맞는 docker image 이름으로 변환 해주는 메서드
     * @param language: 제출한 언어를 입력합니다.
     * @return : 이미지 이름이 반환됩니다.
     */
    private String getImageNameForLanguage(Language language) {
        // 언어에 따른 도커 이미지 이름으로 변환 합니다.
        // 예시 Python의 경우 "python:3.9.18"
        return switch (language) {
            case PYTHON -> "python:3.9.18";
            case JAVA -> "openjdk:11";
            default -> throw new IllegalArgumentException("지원하지 않는 언어 입니다." + language);
        };
    }

     /** 제출한 코드와 input값을 적절하게 결합하여 docker 컨테이너에게 전달하기 위해 가공합니다.
      * 각 언어에 맞게 템플릿을 변환합니다.
     * @param code : 사용자가 제출한 코드를 전달 받습니다. ex) def solution(priorities, location): ....
     * @param input : input은 string으로 입력받습니다.
     * @param language : 체점할 코드의 언어를 전달받습니다.
     * @return : code + Input + 출력 코드를 결합한 코드를 반환합니다.
     */
    private String combineCodeAndInput(String code, String input, Language language) {
        switch (language) {
            case PYTHON -> {
                // Python의 경우, input를 코드에 적절하게 삽입
                return String.format("%s\n\nprint(solution(%s))", code, input);
            }
//            case JAVA -> {
//            }
            default -> throw new IllegalArgumentException("지원하지 않는 언어 입니다." + language);
        }
    }
}
