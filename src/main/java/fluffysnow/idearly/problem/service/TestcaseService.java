package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.problem.compile.ExecuteDocker;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import fluffysnow.idearly.problem.dto.Testcase.TestcaseInfo;
import fluffysnow.idearly.problem.dto.Testcase.TestcaseResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitAndTestcaseCreateRequestDto;
import fluffysnow.idearly.problem.repository.ProblemRepository;
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
public class TestcaseService {
    private final MemberTeamRepository memberTeamRepository;

    private final ProblemRepository problemRepository;

    private final TestcaseRepository testcaseRepository;

    private final ExecuteDocker executeDocker;

    /**
     * 테스트 케이스 실행 메스드
     * 테스트 케이스 hidden이 false인 테스트 케이스만 실행하도록 합니다.
     * @param competitionId: 대회 id
     * @param problemId: 문제 id
     * @param submitAndTestcaseCreateRequestDto: 제출한 team의 code, language를 포함한다.
     */
    @Transactional
    public TestcaseResponseDto ExecuteTestcase(Long competitionId, Long problemId, SubmitAndTestcaseCreateRequestDto submitAndTestcaseCreateRequestDto) {
        // memberId SecurityContextHolder에서 가져옵니다
        Long memberId = getLoginMemberId();

        // competitionId랑 memberId로 memberTeam을 가져옵니다.
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(memberId, competitionId)
                .orElseThrow(() -> new IllegalArgumentException("대회에 참여한 팀을 찾을 수 없습니다."));

        // memberTeam에서 team을 가져옵니다.
        Team team = memberTeam.getTeam();

        Problem problem = problemRepository.findById(problemId).orElseThrow();

        // problemId에 hidden이 false인 테스트 케이스만 가져옵니다.
        List<Testcase> limitedTestCases = testcaseRepository.findNonHiddenTestcaseByProblemId(problemId);

        boolean isCorrect = true;
        // 결과 담을 리스트
        List<TestcaseInfo> testcaseResults = new ArrayList<>();

        for (Testcase testcase : limitedTestCases) {
            String executionResult = executeDocker.executeCode(submitAndTestcaseCreateRequestDto.getCode(), testcase.getInput(), submitAndTestcaseCreateRequestDto.getLanguage());
            log.info("executeTestcase 실행 결과 출력 {}", executionResult);


            // 타임아웃 처리
            if (executionResult.equals("Timeout")) {
                testcaseResults.add(TestcaseInfo.of(testcase.getId(), testcase.getInput(), testcase.getAnswer(), executionResult, "timeout"));
                isCorrect = false;

            } else if (executionResult.startsWith("Error detected:")) {
                // 파이썬 컴파일 에러
                testcaseResults.add(TestcaseInfo.of(testcase.getId(), testcase.getInput(), testcase.getAnswer(), executionResult, "error"));
                isCorrect = false;
            } else if (executionResult.startsWith("Error:")) {
                // 도커 컨테이너 에러
                testcaseResults.add(TestcaseInfo.of(testcase.getId(), testcase.getInput(), testcase.getAnswer(), executionResult, "error"));
                isCorrect = false;
            } else {
                // pass, failed 처리
                String testcaseStatus = executionResult.equals(testcase.getAnswer()) ? "pass" : "failed";
                testcaseResults.add(TestcaseInfo.of(testcase.getId(), testcase.getInput(), testcase.getAnswer(), executionResult, testcaseStatus));
                if (!testcaseStatus.equals("pass")) {
                    isCorrect = false;
                }
            }
        }

        return TestcaseResponseDto.of(isCorrect, testcaseResults);
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
