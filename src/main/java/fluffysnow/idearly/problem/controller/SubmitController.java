package fluffysnow.idearly.problem.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.problem.dto.submit.SubmitResponseDto;
import fluffysnow.idearly.problem.dto.submit.SubmitCreateRequestDto;
import fluffysnow.idearly.problem.service.SubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class SubmitController {
    private final SubmitService submitService;

    /**
     * 알고리즘 문제 제출
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @param submitCreateRequestDto : 요청 바디에 팀이 제출한 소스코드, 선택한 언어를 전달 받습니다.
     * @return : 제출한 코드의 성공여부를 반환합니다.
     */
    @PostMapping("/{competitionId}/problems/{problemId}")
    public ApiResponse<SubmitResponseDto> submitcode(@PathVariable Long competitionId, @PathVariable Long problemId, @RequestBody SubmitCreateRequestDto submitCreateRequestDto) {
        SubmitResponseDto submit = submitService.createSubmit(competitionId, problemId, submitCreateRequestDto);

        return ApiResponse.ok(submit);
    }
}
