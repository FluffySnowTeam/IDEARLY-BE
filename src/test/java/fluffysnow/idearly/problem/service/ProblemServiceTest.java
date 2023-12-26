package fluffysnow.idearly.problem.service;

import fluffysnow.idearly.admin.dto.ProblemCreateRequestDto;
import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.CompetitionCreateRequestDto;
import fluffysnow.idearly.competition.service.CompetitionService;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Submit;
import fluffysnow.idearly.problem.domain.Template;
import fluffysnow.idearly.problem.dto.ProblemResponseDto;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.SubmitRepository;
import fluffysnow.idearly.problem.repository.TemplateRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import fluffysnow.idearly.team.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@Slf4j
public class ProblemServiceTest {
    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TeamRepository TeamRepository;

    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Autowired
    private SubmitRepository submitRepository;


    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired

    private TemplateRepository templateRepository;

    @Test
    @DisplayName("팀 제출이 있다면 마지막 제출 기록을 가져온다.")
    public void getProblemLastSubmitCode() {
        // given
        Long memberId = 1L;
        Long competitionId = 1L;
        Long problemId = 1L;
        Language language = Language.PYTHON;

        // 멤버 생성
        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        Member savedMember = memberRepository.save(member);

        // 대회 생성
        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(competitionCreateRequestDto, savedMember.getId());

        // 문제 생성
        ProblemCreateRequestDto problemCreateRequestDto = new ProblemCreateRequestDto("문제", "문제 설명");
        Problem problem = new Problem(problemCreateRequestDto.getName(), problemCreateRequestDto.getDescription(), competition);
        Problem savedProblem = problemRepository.save(problem);

        // 팀 생성
        Team team = new Team("팀 이름", savedMember, competition);
        Team savedTeam = TeamRepository.save(team);
        MemberTeam memberTeam = new MemberTeam(savedMember, savedTeam, competition);
        memberTeamRepository.save(memberTeam);

        // 템플릿 코드 생성
        Template template = new Template(language, "기본 템플릿 코드", savedProblem);
        templateRepository.save(template);

        // 제출 생성
        Submit submit = Submit.builder()
                .id(1L)
                .correct(true)
                .code("제출 코드")
                .language(Language.PYTHON)
                .team(savedTeam)
                .problem(savedProblem)
                .build();
        submitRepository.save(submit);

        // when
        ProblemResponseDto result = problemService.getProblem(savedMember.getId(), competition.getId(), savedProblem.getId(), language);

        // then
        assertNotNull(result);
        assertEquals("문제", result.getName());
        assertEquals("문제 설명", result.getDescription());
        assertEquals("제출 코드", result.getCode());
     }

    @Test
    @DisplayName("팀 제출이 없다면 기본 템플릿 코드를 가져온다.")
    public void getProblem() {
        // given
        Long memberId = 1L;
        Long competitionId = 1L;
        Long problemId = 1L;
        Language language = Language.PYTHON;

        // 멤버 생성
        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        Member savedMember = memberRepository.save(member);

        // 대회 생성
        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(competitionCreateRequestDto, savedMember.getId());

        // 문제 생성
        ProblemCreateRequestDto problemCreateRequestDto = new ProblemCreateRequestDto("문제", "문제 설명");
        Problem problem = new Problem(problemCreateRequestDto.getName(), problemCreateRequestDto.getDescription(), competition);
        Problem savedProblem = problemRepository.save(problem);

        // 팀 생성
        Team team = new Team("팀 이름", savedMember, competition);
        Team savedTeam = TeamRepository.save(team);
        MemberTeam memberTeam = new MemberTeam(savedMember, savedTeam, competition);
        memberTeamRepository.save(memberTeam);

        // 템플릿 코드 생성
        Template template = new Template(language, "기본 템플릿 코드", savedProblem);
        templateRepository.save(template);

        // 제출 생성
        Submit submit = Submit.builder()
                .id(1L)
                .correct(true)
                .code(null)
                .language(Language.PYTHON)
                .team(savedTeam)
                .problem(savedProblem)
                .build();
        submitRepository.save(submit);

        // when
        ProblemResponseDto result = problemService.getProblem(savedMember.getId(), competition.getId(), savedProblem.getId(), language);

        // then
        assertNotNull(result);
        assertEquals("문제", result.getName());
        assertEquals("문제 설명", result.getDescription());
        assertEquals("기본 템플릿 코드", result.getCode());
    }
}
