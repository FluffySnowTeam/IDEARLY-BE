package fluffysnow.idearly.competition.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionDetailResponseDto {

    private Long competitionId;
    private String competitionTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;

}
