package fluffysnow.idearly.team.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.team.dto.TeamDetailResponseDto;
import fluffysnow.idearly.team.dto.TeamEditRequestDto;
import fluffysnow.idearly.team.dto.TeamInviteAcceptRequestDto;
import fluffysnow.idearly.team.dto.TeamResponseDto;
import fluffysnow.idearly.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    /**
     * 로그인한 멤버가 수락까지 한 소속된 모든 팀 or 초대받은 팀 반환
     * 대신 아직 끝나지 않은 대회여야함
     */
    @GetMapping
    public ApiResponse<List<TeamResponseDto>> getTeamList(@RequestParam("invite") boolean invite) {
        Long loginMemberId = getLoginMemberId();
        List<TeamResponseDto> teamResponseDtoList;
        if (invite) {
            teamResponseDtoList = teamService.getInviteTeamList(loginMemberId); //초대된 팀만 반환
        } else {
            teamResponseDtoList = teamService.getBelongTeamList(loginMemberId); //소속된 팀만 반환
        }

        return ApiResponse.ok(teamResponseDtoList);
    }

    /**
     * 로그인한 멤버의 특정 팀을 조회
     */
    @GetMapping("/{teamId}")
    public ApiResponse<TeamDetailResponseDto> getTeamDetail(@PathVariable("teamId") Long teamId) {
        Long loginMemberId = getLoginMemberId();

        TeamDetailResponseDto teamDetailResponseDto = teamService.getTeamDetail(teamId, loginMemberId);

        return ApiResponse.ok(teamDetailResponseDto);
    }

    /**
     * 팀 초대 수락 혹은 거절
     */
    @PostMapping("/{teamId}")
    public ApiResponse<TeamDetailResponseDto> acceptOrDenyTeamInvite(@RequestBody TeamInviteAcceptRequestDto requestDto,
                                                                     @PathVariable("teamId") Long teamId) {
        Long loginMemberId = getLoginMemberId();

        if (requestDto.isAccept()) {
            teamService.acceptInvite(teamId, loginMemberId);
            TeamDetailResponseDto teamDetailResponseDto = teamService.getTeamDetail(teamId, loginMemberId);
            return ApiResponse.ok(teamDetailResponseDto);
        } else {
            teamService.denyInvite(teamId, loginMemberId);
            return ApiResponse.ok(null);
        }
    }

    /**
     * 팀 구성원을 입력받아 팀 구성원을 재구성
     */
    @PatchMapping("/{teamId}")
    public ApiResponse<TeamDetailResponseDto> inviteAdditionalTeammate(@RequestBody TeamEditRequestDto teamEditRequestDto,
                                         @PathVariable("teamId") Long teamId) {

        Long loginMemberId = getLoginMemberId();

        TeamDetailResponseDto teamDetailResponseDto = teamService.editTeam(teamEditRequestDto, teamId, loginMemberId);

        return ApiResponse.ok(teamDetailResponseDto);
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
