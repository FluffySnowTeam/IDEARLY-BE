package fluffysnow.idearly.competition.controller;


import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.competition.dto.*;
import fluffysnow.idearly.competition.service.CompetitionService;
import fluffysnow.idearly.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ApiResponse<List<CompetitionResponseDto>> getCompetitionList() {

        List<CompetitionResponseDto> competitionResponseDtoList = competitionService.getCompetitionList();

        return ApiResponse.ok(competitionResponseDtoList);
    }

    @GetMapping("/{competitionId}")
    public ApiResponse<CompetitionDetailResponseDto> getCompetitionDetail(@PathVariable("competitionId") Long competitionId) {

        Long loginMemberId = getLoginMemberId();

        CompetitionDetailResponseDto competitionDetailResponseDto = competitionService.getCompetitionDetail(competitionId, loginMemberId);

        return ApiResponse.ok(competitionDetailResponseDto);
    }

    // 대회 참가할 때
    // 팀 이름 중복일 경우 알려줄 수 있어야 함
    // 이메일로 사람 검색해서 name/이메일 돌려줄 수 있어야함
    // 이메일로 사람 검색할 때, 대회에 이미 참가한 회원은 검색할 수 없어야함 -> 이미 다른 팀에 참가한 회원입니다 등을 알려줄 수 있어야함
    @PostMapping("/{competitionId}")
    public ApiResponse<CompetitionParticipateResponseDto> participateInCompetition(@PathVariable("competitionId") Long competitionId,
                                                                                   @RequestBody CompetitionParticipateRequestDto requestDto) {

        Long loginMemberId = getLoginMemberId();
        CompetitionParticipateResponseDto competitionParticipateResponseDto = competitionService.participateInCompetition(competitionId, requestDto, loginMemberId);

        return ApiResponse.ok(competitionParticipateResponseDto);
    }

    /**
     * 해당 대회의 팀에 초대할 때, 초대할 회원을 검색
     */
    @GetMapping("/{competitionId}/members")
    public ApiResponse<InvitableResponseDto> invitableCheck(@PathVariable("competitionId") Long competitionId,
                                                            @RequestParam("email") String email) {

        InvitableResponseDto invitableResponseDto = competitionService.invitableCheck(competitionId, email);

        return ApiResponse.ok(invitableResponseDto);
    }

    // 대기실 입장 API
//    @GetMapping("/{competitionId}/waiting-room")
//    public ApiResponse



    private static Long getLoginMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long loginMemberId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            // 인증 정보 사용
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            loginMemberId = customUserDetails.getId();
        }
        return loginMemberId;
    }
}
