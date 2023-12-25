package fluffysnow.idearly.admin.service;

import fluffysnow.idearly.admin.dto.*;
import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.CompetitionCreateRequestDto;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import fluffysnow.idearly.competition.service.CompetitionService;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.TestcaseRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class AdminServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private TestcaseRepository testcaseRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @Test
    @DisplayName("문제 생성 성공 확인")
    void createProblemTest() {

        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        memberRepository.save(member);
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(competitionCreateRequestDto, adminMember.getId());

        ProblemCreateRequestDto requestDto = new ProblemCreateRequestDto("문제", "문제 설명");
        ProblemCreateResponseDto responseDto = adminService.createProblem(requestDto, competition.getId());
        Problem problem = problemRepository.findById(responseDto.getProblemId()).orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));

        assertEquals(problem.getName(), "문제");
        assertEquals(problem.getDescription(), "문제 설명");
        assertEquals(problem.getCompetition().getId(), competition.getId());
    }

    @Test
    @DisplayName("테스트케이스 생성 성공 확인")
    void createTestcaseTest() {
        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        memberRepository.save(member);
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(competitionCreateRequestDto, adminMember.getId());

        ProblemCreateRequestDto problemCreateRequestDto = new ProblemCreateRequestDto("문제", "문제 설명");
        ProblemCreateResponseDto problemCreateResponseDto = adminService.createProblem(problemCreateRequestDto, competition.getId());
        Problem problem = problemRepository.findById(problemCreateResponseDto.getProblemId()).orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));

        TestcasesInfoDto testcasesInfoDto1 = new TestcasesInfoDto("입력값1", "정답1", true);
        TestcasesInfoDto testcasesInfoDto2 = new TestcasesInfoDto("입력값2", "정답2", false);
        List<TestcasesInfoDto> testcasesInfoList = new ArrayList<>();
        testcasesInfoList.add(testcasesInfoDto1);
        testcasesInfoList.add(testcasesInfoDto2);

        TestcaseCreateRequestDto requestDto = new TestcaseCreateRequestDto(testcasesInfoList);
        adminService.createTestcase(requestDto, problem.getId());
        List<Testcase> testcases = testcaseRepository.findTestCaseByProblemId(problem.getId());

        Testcase testcase1 = testcases.get(0);
        Testcase testcase2 = testcases.get(1);

        assertEquals(testcase1.getInput(), "입력값1");
        assertEquals(testcase1.getAnswer(), "정답1");
        assertTrue(testcase1.isHidden());
        assertEquals(testcase1.getProblem().getId(), problem.getId());

        assertEquals(testcase2.getInput(), "입력값2");
        assertEquals(testcase2.getAnswer(), "정답2");
        assertFalse(testcase2.isHidden());
        assertEquals(testcase2.getProblem().getId(), problem.getId());
    }

    @Test
    @DisplayName("멤버 전체조회 확인")
    void getMemberInformationTest() {
        Member admin = new Member("ccc@naver.com", "관리자", "12345678", Role.ADMIN);
        Member member1 = new Member("aaa@naver.com", "사용자1", "12345678", Role.USER);
        Member member2 = new Member("bbb@naver.com", "사용자2", "12345678", Role.USER);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(admin);
        Member memberInfo1 = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("회원정보가 없습니다"));
        Member memberInfo2 = memberRepository.findByEmail("bbb@naver.com").orElseThrow(() -> new NotFoundException("회원정보가 없습니다"));
        Member adminInfo = memberRepository.findByEmail("ccc@naver.com").orElseThrow(() -> new NotFoundException("회원정보가 없습니다"));

        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(competitionCreateRequestDto, adminInfo.getId());

        Team team = new Team("팀1", memberInfo1, competition);
        Team teamInfo = teamRepository.save(team);

        MemberTeam memberTeam1 = new MemberTeam(memberInfo1, teamInfo, competition);
        MemberTeam memberTeam2 = new MemberTeam(memberInfo2, teamInfo, competition);
        memberTeamRepository.save(memberTeam1);
        memberTeamRepository.save(memberTeam2);

        List<MemberListResponseDto> memberInformation = adminService.getMemberInformation();
        assertEquals(memberInformation.get(0).getMemberId(), memberInfo1.getId());
        assertEquals(memberInformation.get(0).getEmail(), memberInfo1.getEmail());
        assertEquals(memberInformation.get(0).getCompetitionTitleList().get(0), "대회");
        assertEquals(memberInformation.get(0).getTeamNameList().get(0), "팀1");
        assertEquals(memberInformation.get(1).getMemberId(), memberInfo2.getId());
        assertEquals(memberInformation.get(1).getEmail(), memberInfo2.getEmail());
        assertEquals(memberInformation.get(1).getCompetitionTitleList().get(0), "대회");
        assertEquals(memberInformation.get(1).getTeamNameList().get(0), "팀1");
    }
}