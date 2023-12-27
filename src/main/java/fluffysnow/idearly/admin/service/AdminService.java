package fluffysnow.idearly.admin.service;

import fluffysnow.idearly.admin.dto.*;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.TestcaseRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ProblemRepository problemRepository;
    private final CompetitionRepository competitionRepository;
    private final TestcaseRepository testcaseRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;

    @Transactional
    public ProblemCreateResponseDto createProblem(ProblemCreateRequestDto requestDto, Long competitionId) {

        Competition competition = competitionRepository.findById(competitionId).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다."));
        Problem problem = new Problem(requestDto.getName(), requestDto.getDescription(), competition);

        problemRepository.save(problem);

        return ProblemCreateResponseDto.of(problem, competitionId);
    }

    @Transactional
    public TestcaseCreateResponseDto createTestcase(TestcaseCreateRequestDto requestDto, Long problemId) {

        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));
        List<TestcasesInfoDto> testcasesInfo = requestDto.getTestcase();
        log.info("테스트케이스 리스트 dto 생성 완료");

        for (TestcasesInfoDto infoDto : testcasesInfo) {
            log.info(infoDto.toString());
            Testcase testcase = new Testcase(infoDto.getInput(), infoDto.getAnswer(), infoDto.getHidden(), problem);
            log.info(testcase.getInput(), testcase.getAnswer());
            testcaseRepository.save(testcase);
        }

        return TestcaseCreateResponseDto.from(problemId);
    }

    public List<MemberListResponseDto> getMemberInformation() {
        // 회원 전체 조회
        List<Member> members = memberRepository.findAll();

        // 회원 목록을 DTO로 변환
        List<MemberListResponseDto> memberListResponseDtos = members.stream().map(MemberListResponseDto::from).toList();

        // 각 DTO에 대해 competition과 team 정보를 삽입
        memberListResponseDtos.stream().forEach(dto -> {
            Long memberId = dto.getMemberId();
            List<MemberTeam> findMemberTeamList = memberTeamRepository.findAllByMemberId(memberId);

            dto.setCompetitionAndTeam(findMemberTeamList);
        });

        return memberListResponseDtos;
    }

    public List<ProblemsResponseDto> getProblemDetail(Long competitionId) {

        List<Problem> problems = problemRepository.findProblemByCompetitionId(competitionId);

        return ProblemsResponseDto.listFrom(problems);
    }
}
