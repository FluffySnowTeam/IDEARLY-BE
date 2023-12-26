package fluffysnow.idearly.problem.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.problem.dto.ProblemResponseDto;
import fluffysnow.idearly.problem.dto.Testcase.TestcaseResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitAndTestcaseCreateRequestDto;
import fluffysnow.idearly.problem.service.ProblemService;
import fluffysnow.idearly.problem.service.SubmitService;
import fluffysnow.idearly.problem.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class ProblemController {
    private final SubmitService submitService;
    private final TestcaseService testcaseService;
    private final ProblemService problemService;

    /**
     * 알고리즘 문제 조회
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @param language : 언어를 전달 받습니다.
     * @return : 문제 정보를 반환합니다.
     */
    @GetMapping("/{competitionId}/problems/{problemId}")
    public ApiResponse<ProblemResponseDto> getProblem(@PathVariable("competitionId") Long competitionId, @PathVariable("problemId") Long problemId, Language language) {
        Long loginMemberId = getLoginMemberId();
        ProblemResponseDto problem = problemService.getProblem(loginMemberId, competitionId, problemId, language);
        return ApiResponse.ok(problem);
    }

    /**
     * 알고리즘 문제 제출
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @param submitAndTestcaseCreateRequestDto : 요청 바디에 팀이 제출한 소스코드, 선택한 언어를 전달 받습니다.
     * @return : 제출한 코드의 성공여부를 반환합니다.
     */
    @PostMapping("/{competitionId}/problems/{problemId}")
    public ApiResponse<SubmitResponseDto> submitcode(@PathVariable("competitionId") Long competitionId, @PathVariable("problemId") Long problemId, @RequestBody SubmitAndTestcaseCreateRequestDto submitAndTestcaseCreateRequestDto) {
        Long loginMemberId = getLoginMemberId();
        SubmitResponseDto submit = submitService.createSubmit(loginMemberId, competitionId, problemId, submitAndTestcaseCreateRequestDto);

        return ApiResponse.ok(submit);
    }

    /**
     * 알고리즘 문제 테스트
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @param submitAndTestcaseCreateRequestDto : 요청 바디에 팀이 제출한 소스코드, 선택한 언어를 전달 받습니다.
     * @return : 제출한 코드의 성공여부를 반환합니다.
     */
    @PostMapping("/{competitionId}/problems/{problemId}/test")
    public ApiResponse<TestcaseResponseDto> testcode(@PathVariable("competitionId") Long competitionId, @PathVariable("problemId") Long problemId, @RequestBody SubmitAndTestcaseCreateRequestDto submitAndTestcaseCreateRequestDto) {
        TestcaseResponseDto testcaseResponseDto = testcaseService.executeTestcase(problemId, submitAndTestcaseCreateRequestDto);

        return ApiResponse.ok(testcaseResponseDto);
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
