package fluffysnow.idearly.team.controller;

import fluffysnow.idearly.common.ApiResponse;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.team.dto.TeamDetailResponseDto;
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

    @GetMapping
    public ApiResponse<List<TeamResponseDto>> getBelongTeamList() {
        Long loginMemberId = getLoginMemberId();

        List<TeamResponseDto> teamResponseDtoList = teamService.getBelongTeamList(loginMemberId);

        return ApiResponse.ok(teamResponseDtoList);
    }

    @GetMapping("/{teamId}")
    public ApiResponse<TeamDetailResponseDto> getTeamDetail(@PathVariable("teamId") Long teamId) {
        Long loginMemberId = getLoginMemberId();

        TeamDetailResponseDto teamDetailResponseDto = teamService.getTeamDetail(teamId, loginMemberId);

        return ApiResponse.ok(teamDetailResponseDto);
    }

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
