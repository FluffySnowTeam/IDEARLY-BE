package fluffysnow.idearly.team.service;

import fluffysnow.idearly.common.InviteStatus;
import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.CompetitionCreateRequestDto;
import fluffysnow.idearly.competition.dto.CompetitionParticipateRequestDto;
import fluffysnow.idearly.competition.dto.CompetitionParticipateResponseDto;
import fluffysnow.idearly.competition.dto.TeammateRequestDto;
import fluffysnow.idearly.competition.service.CompetitionService;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.member.service.MemberService;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.dto.TeamDetailResponseDto;
import fluffysnow.idearly.team.dto.TeamEditRequestDto;
import fluffysnow.idearly.team.dto.TeamResponseDto;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class TeamServiceTest {

    @Autowired
    private TeamService teamService;
    @Autowired
    private EntityManager em;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private MemberTeamRepository memberTeamRepository;

    @BeforeEach
    void beforeEach() {
        Member member = new Member("aaa@naver.com", "관리자", "12345678", Role.ADMIN);
        memberRepository.save(member);
    }

    @AfterEach
    void afterEach() {
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다!"));
        memberRepository.delete(member);
    }

    @Test
    void getBelongTeamList() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());
        em.flush();
        em.clear();


        List<TeamResponseDto> belongTeamList = teamService.getBelongTeamList(loginMember.getId());

        log.info("loginMember: {}", loginMember.getId());
        log.info("belongTeamList: {}", belongTeamList.toString());

        assertThat(belongTeamList.get(0).getTeamName()).isEqualTo("팀이름1");
        assertThat(belongTeamList.get(0).getCompetitionId()).isEqualTo(competition.getId());
        assertThat(belongTeamList.get(0).getLeaderEmail()).isEqualTo("a@a.com");
    }

    @Test
    void getInviteTeamList() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
        //member와
        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);

        //리더
        Member loginMember1 = memberRepository.findByEmail("a@a.com").get();
        //리더 아닌 팀원
        Member loginMember2 = memberRepository.findByEmail("b@b.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember1.getId());
        em.flush();
        em.clear();


        List<TeamResponseDto> noInviteTeamList = teamService.getInviteTeamList(loginMember1.getId());
        List<TeamResponseDto> inviteTeamList = teamService.getInviteTeamList(loginMember2.getId());

        assertThat(noInviteTeamList.size()).isEqualTo(0);
        assertThat(inviteTeamList.get(0).getCompetitionId()).isEqualTo(competition.getId());
        assertThat(inviteTeamList.get(0).getTeamName()).isEqualTo("팀이름1");
        assertThat(inviteTeamList.get(0).getLeaderEmail()).isEqualTo("a@a.com");
    }

    @Test
    void getTeamDetail() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());
        em.flush();
        em.clear();


        TeamDetailResponseDto teamDetail = teamService.getTeamDetail(participateResponse.getTeamId(), loginMember.getId());


        assertThat(teamDetail.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
        assertThat(teamDetail.getTeammates().get(2).getEmail()).isEqualTo("c@c.com");
        assertThat(teamDetail.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);

    }

    @Test
    void acceptInvite() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());
        em.flush();
        em.clear();


        TeamDetailResponseDto teamDetail = teamService.getTeamDetail(participateResponse.getTeamId(), loginMember.getId());


        assertThat(teamDetail.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
        assertThat(teamDetail.getTeammates().get(2).getEmail()).isEqualTo("c@c.com");
        assertThat(teamDetail.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);

        em.flush();
        em.clear();

        Member acceptMember = memberRepository.findByEmail("b@b.com").get();
        teamService.acceptInvite(participateResponse.getTeamId(), acceptMember.getId());

        em.flush();
        em.clear();

        TeamDetailResponseDto teamDetail2 = teamService.getTeamDetail(participateResponse.getTeamId(), loginMember.getId());
        assertThat(teamDetail2.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail2.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail2.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail2.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail2.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail2.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail2.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail2.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail2.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail2.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail2.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail2.getTeammates().get(2).getEmail()).isEqualTo("c@c.com");
        assertThat(teamDetail2.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
    }

    @Test
    void denyInvite() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());
        em.flush();
        em.clear();


        TeamDetailResponseDto teamDetail = teamService.getTeamDetail(participateResponse.getTeamId(), loginMember.getId());


        assertThat(teamDetail.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
        assertThat(teamDetail.getTeammates().get(2).getEmail()).isEqualTo("c@c.com");
        assertThat(teamDetail.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);

        em.flush();
        em.clear();

        Member denyMember = memberRepository.findByEmail("b@b.com").get();
        teamService.denyInvite(participateResponse.getTeamId(), denyMember.getId());

        em.flush();
        em.clear();

        TeamDetailResponseDto teamDetail2 = teamService.getTeamDetail(participateResponse.getTeamId(), loginMember.getId());
        assertThat(teamDetail2.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail2.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail2.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail2.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail2.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail2.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail2.getTeammates().size()).isEqualTo(2);
        assertThat(teamDetail2.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail2.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail2.getTeammates().get(1).getEmail()).isEqualTo("c@c.com");
        assertThat(teamDetail2.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
    }

    @Test
    void editTeam() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());

        em.flush();
        em.clear();

        MemberCreateRequestDto memberCreateDto4 = new MemberCreateRequestDto("d@d.com", "회원4", "123");
        MemberCreateRequestDto memberCreateDto5 = new MemberCreateRequestDto("e@e.com", "회원5", "123");
        memberService.createUser(memberCreateDto4);
        memberService.createUser(memberCreateDto5);
        TeammateRequestDto teammateRequest4 = new TeammateRequestDto("d@d.com", "회원4");
        List<TeammateRequestDto> teammateRequestDto = new ArrayList<>();
        teammateRequestDto.add(teammateRequest1);
        teammateRequestDto.add(teammateRequest4);

        TeamEditRequestDto teamEditRequestDto = new TeamEditRequestDto(teammateRequestDto);
        TeamDetailResponseDto teamDetail = teamService.editTeam(teamEditRequestDto, participateResponse.getTeamId(), loginMember.getId());

        assertThat(teamDetail.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
        assertThat(teamDetail.getTeammates().get(2).getEmail()).isEqualTo("d@d.com");
        assertThat(teamDetail.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
    }

    @Test
    @DisplayName("이미 수락한 사람의 상태는 유지되어야한다.")
    void editTeam2() {
        Member adminMember = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now().minusDays(1L), LocalDateTime.now().plusDays(1L));
        Competition competition = competitionService.createCompetition(createRequestDto, adminMember.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);
        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());

        Member acceptMember = memberRepository.findByEmail("b@b.com").get();
        teamService.acceptInvite(participateResponse.getTeamId(), acceptMember.getId());

        em.flush();
        em.clear();

        MemberCreateRequestDto memberCreateDto4 = new MemberCreateRequestDto("d@d.com", "회원4", "123");
        MemberCreateRequestDto memberCreateDto5 = new MemberCreateRequestDto("e@e.com", "회원5", "123");
        memberService.createUser(memberCreateDto4);
        memberService.createUser(memberCreateDto5);
        TeammateRequestDto teammateRequest4 = new TeammateRequestDto("d@d.com", "회원4");
        List<TeammateRequestDto> teammateRequestDto = new ArrayList<>();
        teammateRequestDto.add(teammateRequest1);
        teammateRequestDto.add(teammateRequest4);

        em.flush();
        em.clear();

        TeamEditRequestDto teamEditRequestDto = new TeamEditRequestDto(teammateRequestDto);
        TeamDetailResponseDto teamDetail = teamService.editTeam(teamEditRequestDto, participateResponse.getTeamId(), loginMember.getId());

        assertThat(teamDetail.getTeamId()).isEqualTo(participateResponse.getTeamId());
        assertThat(teamDetail.getTeamName()).isEqualTo("팀이름1");
        assertThat(teamDetail.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(teamDetail.getCompetitionTitle()).isEqualTo(competition.getName());
        assertThat(teamDetail.getLeaderName()).isEqualTo("회원1");
        assertThat(teamDetail.getLeaderEmail()).isEqualTo("a@a.com");


        assertThat(teamDetail.getTeammates().size()).isEqualTo(3);
        assertThat(teamDetail.getTeammates().get(0).getEmail()).isEqualTo("a@a.com");
        assertThat(teamDetail.getTeammates().get(0).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(1).getEmail()).isEqualTo("b@b.com");
        assertThat(teamDetail.getTeammates().get(1).getInviteStatus()).isEqualTo(InviteStatus.ACCEPT);
        assertThat(teamDetail.getTeammates().get(2).getEmail()).isEqualTo("d@d.com");
        assertThat(teamDetail.getTeammates().get(2).getInviteStatus()).isEqualTo(InviteStatus.INVITE);
    }
}