package fluffysnow.idearly.team.repository;

import fluffysnow.idearly.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByName(String teamName);

    @Query("select distinct t from Team t join fetch t.memberTeamList join fetch t.competition c where t.id = :teamId")
    Optional<Team> findByIdWithMemberTeam(@Param("teamId") Long teamId);
}
