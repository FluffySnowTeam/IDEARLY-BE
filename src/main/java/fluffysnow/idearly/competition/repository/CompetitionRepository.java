package fluffysnow.idearly.competition.repository;

import fluffysnow.idearly.competition.domain.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
