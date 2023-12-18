package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.problem.domain.Problem;
import fluffysnow.idearly.problem.domain.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Long> {
    @Query("SELECT t FROM Testcase t WHERE t.problem.id = :problemId AND t.hidden = false")
    List<Testcase> findNonHiddenTestcaseByProblemId(Long problemId);

}
