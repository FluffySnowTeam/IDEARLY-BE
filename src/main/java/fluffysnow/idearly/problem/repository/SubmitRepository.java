package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.problem.domain.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
    /**
     * 해당 경진대회를 참여한 팀의 해당 문제의 최신 제출 코드를 가져옵니다.
     * @param problemId : 문제 아이디
     * @param teamId : 팀 아이디
     * @param language : 언어
     * @return : 최신 제출 코드 정보
     */
    @Query("SELECT s from Submit s join fetch s.problem p join fetch s.team t where p.id = :problemId and t.id = :teamId and s.language = :language" +
            " order by s.id desc limit 1")
    Optional<Submit> findByLatestCodeByProblemIdAndTeamIdAndLanguage(@Param("problemId") Long problemId, @Param("teamId") Long teamId, @Param("language") Language language);
}
