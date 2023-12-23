package fluffysnow.idearly.admin.controller;

import fluffysnow.idearly.admin.dto.ProblemCreateRequestDto;
import fluffysnow.idearly.admin.dto.ProblemCreateResponseDto;
import fluffysnow.idearly.admin.dto.TestcaseCreateRequestDto;
import fluffysnow.idearly.admin.dto.TestcaseCreateResponseDto;
import fluffysnow.idearly.admin.service.AdminService;
import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.competition.dto.CompetitionCreateRequestDto;
import fluffysnow.idearly.competition.service.CompetitionService;
import fluffysnow.idearly.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CompetitionService competitionService;
    private final AdminService adminService;

    @PostMapping("/create-competition")
    public ApiResponse<Void> createCompetition(@RequestBody CompetitionCreateRequestDto requestDto) {

        Long loginMemberId = getLoginMemberId();

        competitionService.createCompetition(requestDto, loginMemberId);

        return ApiResponse.ok(null);
    }

    @PostMapping("/create-problem/{competitionId}")
    public ApiResponse<ProblemCreateResponseDto> createProblem(@RequestBody ProblemCreateRequestDto requestDto, @PathVariable("competitionId") Long competitionId) {

        ProblemCreateResponseDto problemResponseDto = adminService.createProblem(requestDto, competitionId);

        return ApiResponse.ok(problemResponseDto);
    }

    @PostMapping("/create-testcase/{problemId}")
    public ApiResponse<TestcaseCreateResponseDto> createTestcase(@RequestBody TestcaseCreateRequestDto requestDto, @PathVariable("problemId") Long problemId) {

        TestcaseCreateResponseDto responseDto = adminService.createTestcase(requestDto, problemId);

        return ApiResponse.ok(responseDto);
    }

    private static Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginMemberId = null;

        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            // 인증 정보 사용
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            loginMemberId = customUserDetails.getMemberId();
        }
        return loginMemberId;
    }
}