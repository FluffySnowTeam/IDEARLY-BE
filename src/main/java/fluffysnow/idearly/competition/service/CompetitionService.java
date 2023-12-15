package fluffysnow.idearly.competition.service;


import fluffysnow.idearly.competition.dto.CompetitionDetailResponseDto;
import fluffysnow.idearly.competition.dto.CompetitionParticipateRequestDto;
import fluffysnow.idearly.competition.dto.CompetitionParticipateResponseDto;
import fluffysnow.idearly.competition.dto.CompetitionResponseDto;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private CompetitionRepository competitionRepository;


    public List<CompetitionResponseDto> getCompetitionList(Long loginMemberId) {
        return null;
    }

    public CompetitionDetailResponseDto getCompetitionDetail(Long competitionId, Long loginMemberId) {
        return null;
    }

    public CompetitionParticipateResponseDto participateInCompetition(Long competitionId, CompetitionParticipateRequestDto requestDto, Long loginMemberId) {
        return null;
    }

}
