package fluffysnow.idearly.problem.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.problem.dto.Testcase.TestcaseResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitAndTestcaseCreateRequestDto;
import fluffysnow.idearly.problem.service.SubmitService;
import fluffysnow.idearly.problem.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class ProblemController {
    private final SubmitService submitService;
    private final TestcaseService testcaseService;

    /**
     * 알고리즘 문제 제출
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @param submitAndTestcaseCreateRequestDto : 요청 바디에 팀이 제출한 소스코드, 선택한 언어를 전달 받습니다.
     * @return : 제출한 코드의 성공여부를 반환합니다.
     */
    @PostMapping("/{competitionId}/problems/{problemId}")
    public ApiResponse<SubmitResponseDto> submitcode(@PathVariable("competitionId") Long competitionId, @PathVariable("problemId") Long problemId, @RequestBody SubmitAndTestcaseCreateRequestDto submitAndTestcaseCreateRequestDto) {
        SubmitResponseDto submit = submitService.createSubmit(competitionId, problemId, submitAndTestcaseCreateRequestDto);

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
        TestcaseResponseDto testcaseResponseDto = testcaseService.ExecuteTestcase(competitionId, problemId, submitAndTestcaseCreateRequestDto);

        return ApiResponse.ok(testcaseResponseDto);
    }
}
