package fluffysnow.idearly.team.dto;

import fluffysnow.idearly.competition.dto.TeammateRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TeamEditRequestDto {

    List<TeammateRequestDto> teammates;

}
