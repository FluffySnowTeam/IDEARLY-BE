package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    /**
     * 해당 경진대회의 문제에 대한 문제 정보를 가져옵니다.
     * @param competitionId : 경진대회 아이디
     * @param problemId : 문제 아이디
     * @return : 문제 정보
     */
    @Query("SELECT p from Problem p join fetch p.competition c where p.id = :problemId and c.id = :competitionId")
    Optional<Problem> findByIdAndCompetitionId(@Param("competitionId") Long competitionId, @Param("problemId") Long problemId);
}
