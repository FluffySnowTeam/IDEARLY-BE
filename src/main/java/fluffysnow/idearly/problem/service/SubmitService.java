package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.problem.compile.ExecuteDocker;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Submit;
import fluffysnow.idearly.problem.domain.Testcase;
import fluffysnow.idearly.problem.dto.submit.SubmitCreateRequestDto;
import fluffysnow.idearly.problem.dto.submit.SubmitResponseDto;
import fluffysnow.idearly.problem.dto.submit.TestCaseInfo;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.SubmitRepository;
import fluffysnow.idearly.problem.repository.TestcaseRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitService {
    private final SubmitRepository submitRepository;

    private final MemberTeamRepository memberTeamRepository;

    private final ProblemRepository problemRepository;

    private final TestcaseRepository testcaseRepository;

    private final ExecuteDocker executeDocker;

    /**
     * 코드 제출 생성 메서드
     * 제출한 team의 문제 id에 대한 제출 정보를 docker 컨테이너 내부에서 처리 후 반환값을 검증하고, 정답여부, 제출 정보를 저장한다.
     * 여러 테스트 케이스 중 첫 세 개의 테스트 케이스만 실행하도록 합니다.
     * @param competitionId: 대회 id
     * @param problemId: 문제 id
     * @param submitCreateRequestDto: 제출한 team의 code, language를 포함한다.
     */
    @Transactional
    public SubmitResponseDto createSubmit(Long competitionId, Long problemId, SubmitCreateRequestDto submitCreateRequestDto) {
        // memberId SecurityContextHolder에서 가져옵니다
        Long memberId = getLoginMemberId();

        // competitionId랑 memberId로 memberTeam을 가져옵니다.
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(memberId, competitionId)
                .orElseThrow(() -> new IllegalArgumentException("대회에 참여한 팀을 찾을 수 없습니다."));

        // memberTeam에서 team을 가져옵니다.
        Team team = memberTeam.getTeam();

        Problem problem = problemRepository.findById(problemId).orElseThrow();

        // problemId에 해당한 전체 testCase를 불러옵니다.
        List<Testcase> limitedTestCases = testcaseRepository.findTestCaseByProblemId(problemId)
                .stream().toList();

        boolean isCorrect = true;
        // 결과 담을 리스트
        List<TestCaseInfo> testcaseResults = new ArrayList<>();

        for (Testcase testcase : limitedTestCases) {
            String executionResult = executeDocker.executeCode(submitCreateRequestDto.getCode(), testcase.getInput(), submitCreateRequestDto.getLanguage());
            log.info("createSubmit 실행 결과 출력 {}", executionResult);


            // 타임아웃 처리
            if (executionResult.equals("Timeout")) {
                testcaseResults.add(TestCaseInfo.of(testcase.getId(), "timeout"));
                isCorrect = false;

            } else if (executionResult.startsWith("Error detected:")) {
                // 파이썬 컴파일 에러
                testcaseResults.add(TestCaseInfo.of(testcase.getId(), "error"));
                isCorrect = false;
            } else if (executionResult.startsWith("Error:")) {
                // 도커 컨테이너 에러
                testcaseResults.add(TestCaseInfo.of(testcase.getId(), "error"));
                isCorrect = false;
            } else {
                // pass, failed 처리
                String testcaseStatus = executionResult.equals(testcase.getAnswer()) ? "pass" : "failed";
                testcaseResults.add(TestCaseInfo.of(testcase.getId(), testcaseStatus));
                if (!testcaseStatus.equals("pass")) {
                    isCorrect = false;
                }
            }
        }

        // 제출정보를 생성
        Submit submit = Submit.builder()
                .team(team)
                .problem(problem)
                .code(submitCreateRequestDto.getCode())
                .language(submitCreateRequestDto.getLanguage())
                .correct(isCorrect)          // 실행결과와 비교해서 ture, false를 입력함.
                .build();

        submitRepository.save(submit);

        // 반환은 correct, testcaseId, status 필요.
        return SubmitResponseDto.of(isCorrect, testcaseResults);
    }

    /**
     * memberId를 SecurityContextHolder에서 가져옵니다.
     * @return : memberId를 반환합니다.
     */
    private static Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginMemberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            // 인증 정보 사용
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            loginMemberId = customUserDetails.getMemberId();
        }
        return loginMemberId;
    }
}
