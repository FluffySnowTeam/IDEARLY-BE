package fluffysnow.idearly.competition.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompetitionParticipateRequestDto {

    private String teamName;
    private List<TeammateRequestDto> teammates;
}
