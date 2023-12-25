package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.problem.domain.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
}
