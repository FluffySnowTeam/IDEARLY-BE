package fluffysnow.idearly.team.service;


import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import fluffysnow.idearly.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;

    // 팀 생성 로직
    public Team createTeam(Competition competition, Member leader, String teamName, List<Member> teammates) {

        // 해당 대회에서 이미 사용중인 팀명이라면 예외 반환
        if (!availableTeamName(competition, teamName)) {
            throw new IllegalArgumentException();   //이름 중복 예외
        }

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

    private boolean availableTeamName(Competition competition, String teamName) {

        List<Team> teamList = teamRepository.findByName(teamName);
        return teamList.stream().filter(t -> t.getCompetition().getId().equals(competition.getId())).findAny().isEmpty();
    }
}
