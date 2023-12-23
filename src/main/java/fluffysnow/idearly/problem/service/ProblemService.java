package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.config.CustomUserDetails;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.dto.ProblemResponseDto;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.SubmitRepository;
import fluffysnow.idearly.problem.repository.TemplateRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final SubmitRepository submitRepository;
    private final TemplateRepository templateRepository;

    /**
     * 해당 경진 대회의 문제에 대한 문제 정보를 가져옵니다.
     * 해당 언어에 대한 팀에서 제출한 코드가 있다면 해당 코드를 가져옵니다.
     * 없다면 기본 템플릿 코드를 제공합니다.
     * @param competitionId : 대회 아이디를 전달 받습니다.
     * @param problemId : 문제 아이디를 전달 받습니다.
     * @return : 문제 정보를 반환합니다.
     */
    @Transactional(readOnly = true)
    public ProblemResponseDto getProblem(Long competitionId, Long problemId, Language language) {
        // 현재 로그인한 사용자의 ID를 가져옵니다.
        Long memberId = getLoginMemberId();

        Problem problem = problemRepository.findByIdAndCompetitionId(competitionId, problemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문제를 찾을수 없습니다."));

        // 현재 로그인한 사용자가 속한 팀을 가져옵니다.
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(memberId, competitionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 경진대회에 참여한 팀을 찾을 수 없습니다."));

        // 해당 언어의 문제에 대한 팀의 마지막 제출을 찾습니다.
        String code = submitRepository.findByLatestCodeByProblemIdAndTeamIdAndLanguage(problemId, memberTeam.getTeam().getId(), language)
                .map(submit -> submit.getCode())
                .orElseGet(() -> {
                    // 해당 언어의 문제에 대한 팀의 마지막 제출이 없다면 기본 템플릿 코드를 제공합니다.
                    return templateRepository.findByProblemIdAndLanguage(problemId, language)
                            .map(template -> template.getCode())
                            .orElseThrow(() -> new IllegalArgumentException("기본 템플릿 코드를 찾을 수 없습니다."));
                });

        return ProblemResponseDto.of(problem.getName(), problem.getDescription(), code);
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
