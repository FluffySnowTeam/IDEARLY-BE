package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Long> {
    // SELECT t FROM Testcase t WHERE t.problemId = :problemId
    List<Testcase> findByProblemId(Long problemId);

}
