package fluffysnow.idearly.problem.compile;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import fluffysnow.idearly.common.Language;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class DockerConfig {
    private final DockerClient dockerClient;

    public DockerConfig() {
        // docker 클라이언트 생성
        DockerClientConfig standard = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(standard.getDockerHost())       // 호스트 설정
                .sslConfig(standard.getSSLConfig())
                .maxConnections(100)                  // 최대 동시 연결 수 설정
                .connectionTimeout(Duration.ofSeconds(30))  // 연결 시도 타임아웃 최대 30초 설정
                .responseTimeout(Duration.ofSeconds(30))    // 응답 대기 시간 30초 설정
                .build();

        this.dockerClient = DockerClientImpl.getInstance(standard, httpClient);
    }

    /**
     * 도커 이미지를 받아옵니다.
     * @param imageName: 받아올 이미지의 이름을 지정합니다.
     */
    private void pullImage(String imageName) {
        try {
            dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /**
     * 도커 컨테이너를 생성합니다.
     * @param excutableCode : 실행할 코드를 전달합니다.
     * @param imageName : 도커 이미지 네임을 전달합니다.
     * @param language : 사용할 언어를 입력합니다.
     * @return : 생성된 컨테이너 아이디를 반환합니다.
     */
    public String createContainer(String excutableCode, String imageName, Language language) {
        // 이미지를 받아옵니다.
        pullImage(imageName);

        String[] command = getCommandForLanguage(excutableCode, language);
        // 도커 컨테이너 생성합니다.
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withCmd(command)
                .exec();

        log.trace("도커 컨테이너 생성 container id = {}", container.getId());
        return container.getId();
    }

    /**
     * 도커 컨테이너에게 전달할 명령어를 각언어에 맞게 반환해줍니다.
     * @param excutableCode : 실행할 코드를 전달합니다.
     * @param language : 반환할 언어를 전달합니다.
     * @return : 도커 컨테이너에서 실행할 컴파일 명령어를 반환합니다.
     */
    private String[] getCommandForLanguage(String excutableCode, Language language) {
        return switch (language) {
            case PYTHON -> new String[]{"python", "-c", excutableCode};
            case JAVA ->
                    new String[]{"/bin/sh", "-c", "echo '" + excutableCode + "' > Main.java && javac Main.java && java Main"};
            // 다른 언어에 대한 케이스 추가...
            default -> throw new IllegalArgumentException("지원하지 않는 언어: " + language);
        };
    }

    /**
     * 해당 도커 컨테이너를 실행합니다.
     * @param containerId:실행할 컨테이너 아이디를 입력합니다.
     */
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 해당 도커 컨테이너를 정지 및 삭제합니다.
     * @param containerId: 정지 및 삭제할 컨테이너 아이디를 입력해줍니다.
     */
    public void stopAndRemoveContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();

        log.trace("도커 컨테이너 정지 및 삭제 container id = {}", containerId);
    }

    /**
     * 해당 컨테이너의 로그를 출력합니다.
     * @param containerId: 로그를 출력한 컨테이너 아이디를 입력합니다.
     * @code @return:  컨테이너 실행 결과 값을 반환합니다.
     */
    public String getContainerLogs(String containerId) {
        StringBuilder logs = new StringBuilder();

        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)     // 로그가 실시간으로 생성 될때마다 새로운 로그를 가져옴.
                .withTailAll()              // 기존에 존재하는 로그의 마지막 부분부터 로그르 가져옴.
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame item) {
                        logs.append(new String(item.getPayload()));
                    }
                });
        return logs.toString();
    }
}