package fluffysnow.idearly.team.repository;

import fluffysnow.idearly.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByName(String teamName);
}
