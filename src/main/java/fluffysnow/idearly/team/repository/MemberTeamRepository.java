package fluffysnow.idearly.team.repository;


import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.MemberTeamPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberTeamRepository extends JpaRepository<MemberTeam, MemberTeamPK> {

    @Query("select mt from MemberTeam mt where mt.member.id = :memberId and mt.team.id = :teamId")
    Optional<MemberTeam> findByMemberIdAndTeamId(@Param("memberId") Long memberId, @Param("teamId") Long teamId);


    @Query("select mt from MemberTeam mt join fetch mt.team t join fetch mt.competition c where mt.member.id = :memberId")
    List<MemberTeam> findByMemberIdWithTeamAndCompetition(@Param("memberId") Long memberId);

    @Query("select mt from MemberTeam mt join fetch mt.team t join fetch mt.competition c" +
            " where mt.member.id = :memberId and mt.competition.id = :competitionId")
    Optional<MemberTeam> findByMemberIdAndCompetitionId(@Param("memberId")Long memberId, @Param("competitionId")Long competitionId);
}
