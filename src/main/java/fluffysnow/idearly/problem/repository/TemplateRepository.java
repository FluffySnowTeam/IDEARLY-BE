package fluffysnow.idearly.problem.repository;

import fluffysnow.idearly.common.Language;
import fluffysnow.idearly.problem.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    /**
     * 경진대회의 문제에 대한 템플릿 코드 정보를 가져옵니다.
     * @param problemId : 문제 아이디
     * @param language : 언어
     * @return : 템플릿 코드 정보
     */
    @Query("SELECT t from Template t join fetch t.problem p where p.id = :problemId and t.language = :language")
    Optional<Template> findByProblemIdAndLanguage(@Param("problemId") Long problemId, @Param("language") Language language);
}
