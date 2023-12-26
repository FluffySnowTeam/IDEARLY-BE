package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.dto.ProblemIdListResponseDto;
import fluffysnow.idearly.problem.dto.ProblemResponseDto;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.SubmitRepository;
import fluffysnow.idearly.problem.repository.TemplateRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public ProblemResponseDto getProblem(Long loginMemberId, Long competitionId, Long problemId, Language language) {
        Problem problem = problemRepository.findByIdAndCompetitionId(competitionId, problemId)
                .orElseThrow(() -> new NotFoundException("해당 문제를 찾을수 없습니다."));

        // 현재 로그인한 사용자가 속한 팀을 가져옵니다.
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(loginMemberId, competitionId)
                .orElseThrow(() -> new NotFoundException("해당 경진대회에 참여한 팀을 찾을 수 없습니다."));

        // 해당 언어의 문제에 대한 팀의 마지막 제출을 찾습니다.
        String code = submitRepository.findByLatestCodeByProblemIdAndTeamIdAndLanguage(problemId, memberTeam.getTeam().getId(), language)
                .map(submit -> submit.getCode())
                .orElseGet(() -> {
                    // 해당 언어의 문제에 대한 팀의 마지막 제출이 없다면 기본 템플릿 코드를 제공합니다.
                    return templateRepository.findByProblemIdAndLanguage(problemId, language)
                            .map(template -> template.getCode())
                            .orElseThrow(() -> new NotFoundException("기본 템플릿 코드를 찾을 수 없습니다."));
                });

        return ProblemResponseDto.of(problem.getName(), problem.getDescription(), code);
    }


    public ProblemIdListResponseDto getProblemIdList(Long competitionId) {

        List<Problem> problems = problemRepository.findProblemByCompetitionId(competitionId);

        return ProblemIdListResponseDto.from(problems);
    }
}
