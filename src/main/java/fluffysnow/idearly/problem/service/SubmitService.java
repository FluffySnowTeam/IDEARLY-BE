package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.problem.compile.ExcuteDocker;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitService {
    private final SubmitRepository submitRepository;

    private final MemberTeamRepository memberTeamRepository;

    private final ProblemRepository problemRepository;

    private final TestcaseRepository testcaseRepository;

    private final ExcuteDocker excuteDocker;

    /**
     * 코드 제출 생성 메서드
     * 제출한 team의 문제 id에 대한 제출 정보를 docker 컨테이너 내부에서 처리 후 반환값을 검증하고, 정답여부, 제출 정보를 저장한다.
     * @param competitionId: 대회 id
     * @param problemId: 문제 id
     * @param submitCreateRequestDto: 제출한 team의 code, language를 포함한다.
     */
    @Transactional
    public SubmitResponseDto createSubmit(Long competitionId, Long problemId, SubmitCreateRequestDto submitCreateRequestDto) {
        // memberId SecurityContextHolder에서 가져옴.
        Long memberId = getLoginMemberId();

        // competitionId랑 memberId로 memberTeam을 가져옴.
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(memberId, competitionId)
                .orElseThrow(() -> new IllegalArgumentException("대회에 참여한 팀을 찾을 수 없습니다."));

        // memberTeam에서 team을 가져옴.
        Team team = memberTeam.getTeam();

        Problem problem = problemRepository.findById(problemId).orElseThrow();

        List<Testcase> testcases = testcaseRepository.findByProblemId(problemId);

        boolean isCorrect = true;
        // 결과 담을 리스트
        List<TestCaseInfo> testcaseResults = new ArrayList<>();

        for (Testcase testcase : testcases) {
            // code, input, lnaguage  정답과 비교하는 부분은 서비스단으로 빠져야하나...?
            String executionResult = excuteDocker.executeCode(submitCreateRequestDto.getCode(), testcase.getInput(), submitCreateRequestDto.getLanguage());

            // 결과 검증 부분 필요.
            // 수정: error, timeout 로직 필요.
            String testcaseStatus = executionResult.equals(testcase.getAnswer()) ? "pass" : "failed";
            testcaseResults.add(TestCaseInfo.of(testcase.getId(), testcaseStatus));

            if (!testcaseStatus.equals("pass")) {
                isCorrect = false;
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

        Submit savedSubmit = submitRepository.save(submit);

        // 반환은 correct, testcaseId, status 필요.
        return SubmitResponseDto.of(isCorrect, testcaseResults);
    }

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
