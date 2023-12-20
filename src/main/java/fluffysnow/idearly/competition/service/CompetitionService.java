package fluffysnow.idearly.competition.service;


import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.*;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import fluffysnow.idearly.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final TeamService teamService;


    /**
     * 메인페이지에서 대회 리스트를 조회
     */
    @Transactional(readOnly = true)
    public List<CompetitionResponseDto> getCompetitionList() {

        List<Competition> competitionList = competitionRepository.findAll();
        return CompetitionResponseDto.listFrom(competitionList);
    }

    /**
     * 특정 대회에 대한 상세정보를 조회
     */
    @Transactional(readOnly = true)
    public CompetitionDetailResponseDto getCompetitionDetail(Long competitionId, Long loginMemberId) {

        if (loginMemberId == null) {
            Competition findCompetition = competitionRepository.findById(competitionId).orElseThrow(() -> new NotFoundException("존재하지 않는 대회입니다."));  //notFound
            return CompetitionDetailResponseDto.of(findCompetition, false, null);
        } else {
            Competition findCompetition = competitionRepository.findById(competitionId).orElseThrow(() -> new NotFoundException("존재하지 않는 대회입니다."));  //notFound
            MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndCompetitionId(loginMemberId, competitionId).orElse(null);
            return CompetitionDetailResponseDto.of(findCompetition, true, memberTeam);
        }
    }

    /**
     * 새로운 팀을 만들어서 참가
     */
    public CompetitionParticipateResponseDto participateInCompetition(Long competitionId, CompetitionParticipateRequestDto requestDto, Long loginMemberId) {

        // 대회와 회원 정보 조회
        Competition competition = competitionRepository.findById(competitionId).orElseThrow(() -> new NotFoundException("존재하지 않는 대회입니다."));  //notFound
        Member loginMember = memberRepository.findById(loginMemberId).orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));    //notFound
        String teamName = requestDto.getTeamName();

        // 초대할 팀원의 이메일을 기준으로 팀원 리스트 생성
        List<Member> teammates = requestDto
                .getTeammates()
                .stream()
                .map(TeammateRequestDto::getEmail)
                .map(email -> memberRepository.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다.")))
                .toList();  //notFound

        // 위 정보를 기준으로 팀 생성
        Team team = teamService.createTeam(competition, loginMember, teamName, teammates);

        // 생성된 팀을 기준으로 팀 생성 응답 전달
        return CompetitionParticipateResponseDto.of(competition, team);
    }

    /**
     * email로 멤버 검색시에 해당 멤버 이름/이메일과 함께 초대 가능 여부를 반환
     */
    @Transactional(readOnly = true)
    public InvitableResponseDto invitableCheck(Long competitionId, String email) {

        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return InvitableResponseDto.of(null, false);
        }

        Member findMember = memberOptional.get();
        boolean invitable = memberTeamRepository.findByMemberIdAndCompetitionId(findMember.getId(), competitionId).isEmpty();
        return InvitableResponseDto.of(findMember, invitable);
    }

    public Competition createCompetition(CompetitionCreateRequestDto requestDto, Member adminMember) {

        Competition competition = new Competition(requestDto.getTitle(), requestDto.getDescription(), requestDto.getStartDateTime(), requestDto.getEndDateTime(), adminMember);

        return competitionRepository.save(competition);
    }
}
