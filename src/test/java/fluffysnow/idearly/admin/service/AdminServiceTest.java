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
    private CompetitionRepository competitionRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private TestcaseRepository testcaseRepository;

    @Test
    @DisplayName("문제 생성 성공 확인")
    void createProblemTest() {

        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        memberRepository.save(member);
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        CompetitionCreateRequestDto competitionCreateRequestDto = new CompetitionCreateRequestDto("대회", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        competitionService.createCompetition(competitionCreateRequestDto, adminMember.getId());
        Competition competition = competitionRepository.findByName("대회").orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다."));

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
        competitionService.createCompetition(competitionCreateRequestDto, adminMember.getId());
        Competition competition = competitionRepository.findByName("대회").orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다."));
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
}