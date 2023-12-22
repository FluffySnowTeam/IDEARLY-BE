package fluffysnow.idearly.admin.service;

import fluffysnow.idearly.admin.dto.*;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import fluffysnow.idearly.problem.repository.ProblemRepository;
import fluffysnow.idearly.problem.repository.TestcaseRepository;
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

    public ProblemCreateResponseDto createProblem(ProblemCreateRequestDto requestDto, Long competitionId) {

        Competition competition = competitionRepository.findById(competitionId).orElseThrow(() -> new NotFoundException("대회를 찾을 수 없습니다."));
        Problem problem = new Problem(requestDto.getName(), requestDto.getDescription(), competition);

        problemRepository.save(problem);

        return ProblemCreateResponseDto.of(problem, competitionId);
    }

    public TestcaseCreateResponseDto createTestcase(TestcaseCreateRequestDto requestDto, Long problemId) {

        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));
        List<TestcasesInfoDto> testcasesInfo = requestDto.getTestcase();

        for (TestcasesInfoDto infoDto : testcasesInfo) {
            Testcase testcase = new Testcase(infoDto.getInput(), infoDto.getAnswer(), infoDto.getHidden(), problem);
            testcaseRepository.save(testcase);
        }

        return TestcaseCreateResponseDto.from(problemId);
    }
}
