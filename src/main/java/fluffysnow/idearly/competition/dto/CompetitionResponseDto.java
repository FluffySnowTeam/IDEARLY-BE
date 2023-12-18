package fluffysnow.idearly.competition.dto;

import fluffysnow.idearly.competition.domain.Competition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionResponseDto {

    private Long competitionId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private static CompetitionResponseDto from(Competition competition) {
        return new CompetitionResponseDto(
                competition.getId(),
                competition.getName(),
                competition.getStartDatetime(),
                competition.getEndDatetime()
        );
    }

    public static List<CompetitionResponseDto> listFrom(List<Competition> competitions) {
        return competitions.stream().map(CompetitionResponseDto::from).toList();
    }
}
