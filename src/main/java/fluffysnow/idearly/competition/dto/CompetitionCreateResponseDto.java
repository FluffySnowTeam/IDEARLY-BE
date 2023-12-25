package fluffysnow.idearly.competition.dto;

import fluffysnow.idearly.competition.domain.Competition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionCreateResponseDto {

    private Long competitionId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;

    public static CompetitionCreateResponseDto from(Competition competition) {
        return new CompetitionCreateResponseDto(
                competition.getId(),
                competition.getName(),
                competition.getStartDatetime(),
                competition.getEndDatetime(),
                competition.getDescription()
        );
    }

}
