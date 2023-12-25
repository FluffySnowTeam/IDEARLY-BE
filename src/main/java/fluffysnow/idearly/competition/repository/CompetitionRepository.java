package fluffysnow.idearly.competition.repository;

import fluffysnow.idearly.competition.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Query("select c from Competition c where c.endDatetime >= :now order by c.startDatetime")
    List<Competition> findAvailableCompetitionList(@Param("now") LocalDateTime now);

    @Query("select c from Competition c where c.endDatetime < :now order by c.startDatetime")
    List<Competition> findUnavailableCompetitionList(@Param("now") LocalDateTime now);
}
