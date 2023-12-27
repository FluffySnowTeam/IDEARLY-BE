package fluffysnow.idearly.team.service;


import fluffysnow.idearly.common.exception.BadRequestException;
import fluffysnow.idearly.common.exception.ForbiddenException;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.common.exception.TeamNameDuplicateException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.TeammateRequestDto;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import fluffysnow.idearly.team.dto.TeamDetailResponseDto;
import fluffysnow.idearly.team.dto.TeamEditRequestDto;
import fluffysnow.idearly.team.dto.TeamInviteAcceptResponseDto;
import fluffysnow.idearly.team.dto.TeamResponseDto;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import fluffysnow.idearly.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final MemberRepository memberRepository;

    // 팀 생성 로직
    public Team createTeam(Competition competition, Member leader, String teamName, List<Member> teammates) {

        // 해당 대회에서 이미 사용중인 팀명이라면 예외 반환
        if (!availableTeamName(competition, teamName)) {
            throw new TeamNameDuplicateException("이미 존재하는 팀 이름입니다.");   //이름 중복 예외
        }

        Optional<MemberTeam> memberTeamOptional = memberTeamRepository.findByMemberIdAndCompetitionId(leader.getId(), competition.getId());
        if (memberTeamOptional.isPresent()) {
            throw new BadRequestException("이미 해당 대회에서 소속된 팀이나 초대받은 팀이 존재합니다.");
        }
        teammates.forEach(tm -> {
            Optional<MemberTeam> teammateTeamOptional = memberTeamRepository.findByMemberIdAndCompetitionId(tm.getId(), competition.getId());
            if (teammateTeamOptional.isPresent()) {
                throw new BadRequestException("초대할 수 없는 팀원입니다.");
            }
        });


        // 팀 이름, 팀장, 대회 정보를 기준으로 팀 생성 및 저장
        Team team = new Team(teamName, leader, competition);
        Team savedTeam = teamRepository.save(team);

        // 팀 생성 후에 팀장을 팀원으로 소속
        MemberTeam memberTeam = new MemberTeam(leader, team, competition);
        memberTeam.acceptInvitation();  // 리더는 바로 팀으로 소속
        memberTeamRepository.save(memberTeam);

        // 팀원들을 팀에 초대 및 저장
        teammates.stream().map(teammate -> new MemberTeam(teammate, team, competition)).forEach(memberTeamRepository::save);

        return savedTeam;
    }

    // 가능한 팀명인지를 확인
    private boolean availableTeamName(Competition competition, String teamName) {

        List<Team> teamList = teamRepository.findByName(teamName);
        return teamList.stream().filter(t -> t.getCompetition().getId().equals(competition.getId())).findAny().isEmpty();
    }


    // 특정 멤버의 소속된 팀 리스트 반환
    @Transactional(readOnly = true)
    public List<TeamResponseDto> getBelongTeamList(Long loginMemberId) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        List<MemberTeam> memberTeamList = memberTeamRepository.findAvailableBelongMemberTeamByMemberId(loginMemberId, now);
        List<Team> teams = memberTeamList.stream().map(MemberTeam::getTeam).toList();

        return TeamResponseDto.listFrom(teams);
    }
    // 특정 멤버가 초대받은 팀 리스트 반환
    @Transactional(readOnly = true)
    public List<TeamResponseDto> getInviteTeamList(Long loginMemberId) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        List<MemberTeam> memberTeamList = memberTeamRepository.findAvailableInviteMemberTeamByMemberId(loginMemberId, now);
        List<Team> teams = memberTeamList.stream().map(MemberTeam::getTeam).toList();

        return TeamResponseDto.listFrom(teams);
    }

    // 특정 멤버의 소속된 특정 팀 반환
    @Transactional(readOnly = true)
    public TeamDetailResponseDto getTeamDetail(Long teamId, Long loginMemberId) {

        Team findTeam = teamRepository.findByIdWithMemberTeam(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));    //NotFound
        boolean memberTeamMatch = findTeam.getMemberTeamList().stream().anyMatch(mt -> mt.getMember().getId().equals(loginMemberId));
        if (!memberTeamMatch) {
            throw new ForbiddenException("다른 멤버의 팀에 접근하려는 시도가 발생했습니다.");  // 다른 사람이 팀을 검색 - Forbidden
        }

        return TeamDetailResponseDto.from(findTeam);
    }

    // 초대 수락
    public TeamInviteAcceptResponseDto acceptInvite(Long teamId, Long loginMemberId) {
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndTeamId(loginMemberId, teamId).orElseThrow(() -> new NotFoundException("회원 정보와 팀 정보가 일치하지 않습니다."));  //NotFound
        memberTeam.acceptInvitation();
        return TeamInviteAcceptResponseDto.from(teamId, true);
    }

    // 초대 거절
    public TeamInviteAcceptResponseDto denyInvite(Long teamId, Long loginMemberId) {
        MemberTeam memberTeam = memberTeamRepository.findByMemberIdAndTeamId(loginMemberId, teamId).orElseThrow(() -> new NotFoundException("회원 정보와 팀 정보가 일치하지 않습니다."));  //NotFound
        memberTeamRepository.delete(memberTeam);
        return TeamInviteAcceptResponseDto.from(teamId, false);
    }

    // 팀 구성원 변경
    public TeamDetailResponseDto editTeam(TeamEditRequestDto teamEditRequestDto, Long teamId, Long leaderId) {


        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));  //notFound
        Competition competition = team.getCompetition();

        List<String> teammateEmailList = teamEditRequestDto.getTeammates().stream().map(TeammateRequestDto::getEmail).toList();
        List<MemberTeam> memberTeamList = memberTeamRepository.findByTeamIdAndCompetitionId(team.getId(), competition.getId());
        List<String> existingEmailList = memberTeamList.stream().map(mt -> mt.getMember().getEmail()).toList();

        // 강퇴당한 멤버 삭제
        List<MemberTeam> removeMemberTeamList = memberTeamList.stream()
                .filter(mt -> !(teammateEmailList.contains(mt.getMember().getEmail())))
                .filter(mt -> !(Objects.equals(mt.getMember().getId(), leaderId)))  // 팀장은 무조건 제외
                .toList();
        memberTeamRepository.deleteAll(removeMemberTeamList);
        removeMemberTeamList.forEach(mt -> team.getMemberTeamList().remove(mt));

        // 새로 초대된 멤버 추가
        List<TeammateRequestDto> addMemberTeamRequestDto = teamEditRequestDto.getTeammates().stream()
                .filter(tm -> !(existingEmailList.contains(tm.getEmail())))
                .toList();
        List<Member> teammates = addMemberTeamRequestDto.stream()
                .map(TeammateRequestDto::getEmail)
                .map(memberRepository::findByEmail)
                .map(optionalMember -> optionalMember.orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다.")))    //notFound
                .toList();

        teammates.stream().map(teammate -> new MemberTeam(teammate, team, competition)).forEach(memberTeamRepository::save);

        return TeamDetailResponseDto.from(team);
    }
}
