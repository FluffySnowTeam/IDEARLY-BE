package fluffysnow.idearly.problem.compile;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import fluffysnow.idearly.common.Language;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class DockerConfig {
    private final DockerClient dockerClient;

    public DockerConfig() {

        // docker 클라이언트 생성
        DockerClientConfig standard = DefaultDockerClientConfig.createDefaultConfigBuilder().build();


        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(standard.getDockerHost())       // 호스트 설정
                .sslConfig(standard.getSSLConfig())
//                .connectTimeout(100)                  // 최대 동시 연결 수 설정
                .build();

        this.dockerClient = DockerClientImpl.getInstance(standard, httpClient);
    }

    /**
     * 도커 이미지를 받아옵니다.
     *
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
     *
     * @param excutableCode : 실행할 코드를 전달합니다.
     * @param imageName     : 도커 이미지 네임을 전달합니다.
     * @param language      : 사용할 언어를 입력합니다.
     * @return : 생성된 컨테이너 아이디를 반환합니다.
     */
    public String createContainer(String excutableCode, String imageName, Language language) {
        HostConfig hostConfig = new HostConfig()
                .withAutoRemove(true)
                .withNetworkMode("host");

        // 이미지를 받아옵니다.
        pullImage(imageName);

        String[] command = getCommandForLanguage(excutableCode, language);
        log.info("커맨드 생성 완료: {}", command.toString());
        // 도커 컨테이너 생성합니다.
        log.info("도커 명령어 실행");
        CreateContainerResponse container = null;
        try {
            container = dockerClient.createContainerCmd(imageName)
                    .withHostConfig(hostConfig)
                    .withCmd(command)
                    .exec();
        } catch (Exception e) {
            log.error("e", e);
        }
        log.info("커맨드 실행 완료");
        log.trace("도커 컨테이너 생성 container id = {}", container.getId());
        return container.getId();
    }

    /**
     * 도커 컨테이너에게 전달할 명령어를 각언어에 맞게 반환해줍니다.
     *
     * @param excutableCode : 실행할 코드를 전달합니다.
     * @param language      : 반환할 언어를 전달합니다.
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
     *
     * @param containerId:실행할 컨테이너 아이디를 입력합니다.
     */
    public void startContainer(String containerId) {

        dockerClient.startContainerCmd(containerId).exec();
    }

    /**
     * 해당 도커 컨테이너를 정지 및 삭제합니다.
     *
     * @param containerId: 정지 및 삭제할 컨테이너 아이디를 입력해줍니다.
     */
    public void stopAndRemoveContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();

        } catch (NotModifiedException e) {
            log.debug("already clossed Container = {}", containerId);
        }

        dockerClient.removeContainerCmd(containerId).exec();
        log.trace("도커 컨테이너 정지 및 삭제 container id = {}", containerId);
    }

    /**
     * 해당 컨테이너의 로그를 출력합니다.
     * @param containerId: 로그를 출력한 컨테이너 아이디를 입력합니다.
     * @code @return:  컨테이너 실행 결과 값을 반환합니다.
     */
    public String getContainerLogs(String containerId) {
        log.info("로그 컨테이너 생성 직전");
        StringBuilder resultLogBuilder = new StringBuilder();
        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)     // 로그가 실시간으로 생성 될때마다 새로운 로그를 가져옴.
                    .withTailAll()              // 기존에 존재하는 로그의 마지막 부분부터 로그르 가져옴.
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame item) {
                            String resultLine = new String(item.getPayload());
                            resultLogBuilder.append(resultLine);
                        }
                    }).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String result = resultLogBuilder.toString();
        log.info("로그 컨테이너 출력 결과 {}", result);

        // 컴파일 에러 처리 부분(임시)
        if (result.contains("Error")) {
            return "Error detected: " + result;
        } else {
            String[] split = result.split("\n");
            result = split[0];

            return result;
        }
    }


    /**
     * 도커 컨테이너를 실행하고 지정된 시간 내에 작업을 완료합니다.
     * 시간 초과 시 컨테이너를 중지시킵니다.
     * @param containerId : 실행할 컨테이너 아이디를 입력합니다.
     * @param timeout     : 최대 실행 시간을 입력합니다.
     * @param timeUnit    : 사간 단위를 지정합니다.
     * @return : 실행 결과를 반환 합니다.
     */
    public String executeContainerWithTimeout(String containerId, long timeout, TimeUnit timeUnit){
        log.info("커맨드 실행 직전");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            startContainer(containerId);
            return getContainerLogs(containerId);
        });

        try {
            // Future 객체를 사용해 지정된 시간 동안 컨테이너 로그를 기다림.
            return future.get(timeout, timeUnit);

        } catch (TimeoutException e) {
            // 시간 초과 발생
            log.error("excuteContainerwithTimeout 컨테이너 실행 타임아웃: {} {}", containerId, e.getMessage());
            stopAndRemoveContainer(containerId);
            return "Timeout";

        } catch (ExecutionException | InterruptedException e) {
            log.error("excuteContainerwithTimeout 컨테이너 실행 중 오류 발생 : {} {}", containerId, e.getMessage());
            throw new RuntimeException("Error:" + e.getMessage());

        } finally {
            executor.shutdown();
        }
    }
}