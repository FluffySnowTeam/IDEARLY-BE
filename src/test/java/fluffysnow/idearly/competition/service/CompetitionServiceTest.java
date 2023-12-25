package fluffysnow.idearly.competition.service;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.common.exception.NotFoundException;
import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.competition.dto.*;
import fluffysnow.idearly.competition.repository.CompetitionRepository;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.member.dto.MemberCreateRequestDto;
import fluffysnow.idearly.member.repository.MemberRepository;
import fluffysnow.idearly.member.service.MemberService;
import fluffysnow.idearly.team.repository.MemberTeamRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@Transactional
@SpringBootTest
@Slf4j
class CompetitionServiceTest {

    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
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
    void createCompetition() {
        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        Competition competition = competitionService.createCompetition(createRequestDto, member.getId());

        assertThat(competition.getId()).isNotNull();
    }

    @Test
    void competitionList() {
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        CompetitionCreateRequestDto createRequestDto1 = new CompetitionCreateRequestDto("대회 이름1", "대회 설명1", LocalDateTime.now(), LocalDateTime.now().plusDays(1L));
        Competition competition1 = competitionService.createCompetition(createRequestDto1, member.getId());

        CompetitionCreateRequestDto createRequestDto2 = new CompetitionCreateRequestDto("대회 이름2", "대회 설명2", LocalDateTime.now(), LocalDateTime.now().plusDays(1L));
        Competition competition2 = competitionService.createCompetition(createRequestDto2, member.getId());

        CompetitionCreateRequestDto createRequestDto3 = new CompetitionCreateRequestDto("대회 이름3", "대회 설명3", LocalDateTime.now().minusDays(1L), LocalDateTime.now().minusDays(1L));
        Competition competition3 = competitionService.createCompetition(createRequestDto3, member.getId());

        em.flush();
        em.clear();

        List<CompetitionResponseDto> availableCompetitionList = competitionService.getCompetitionList(true);
        assertThat(availableCompetitionList.size()).isEqualTo(2);

        assertThat(availableCompetitionList.get(0).getCompetitionId()).isEqualTo(competition1.getId());
        assertThat(availableCompetitionList.get(1).getCompetitionId()).isEqualTo(competition2.getId());

        List<CompetitionResponseDto> unavailableCompetitionList = competitionService.getCompetitionList(false);
        assertThat(unavailableCompetitionList.size()).isEqualTo(1);

        assertThat(unavailableCompetitionList.get(0).getCompetitionId()).isEqualTo(competition3.getId());
    }

    @Test
    void competitionDetail() {
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(createRequestDto, member.getId());

        em.flush();
        em.clear();

        CompetitionDetailResponseDto competitionDetailResponse = competitionService.getCompetitionDetail(competition.getId(), null);

        assertThat(competitionDetailResponse.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(competitionDetailResponse.getTitle()).isEqualTo(competition.getName());
        assertThat(competitionDetailResponse.getDescription()).isEqualTo(competition.getDescription());
        assertThat(competitionDetailResponse.isLogin()).isFalse();
        assertThat(competitionDetailResponse.getTeamId()).isNull();
        assertThat(competitionDetailResponse.getTeamName()).isNull();

    }

    @Test
    void participateInCompetition() {
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(createRequestDto, member.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);

        em.flush();
        em.clear();

        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());

        assertThat(participateResponse.getCompetitionId()).isEqualTo(competition.getId());
        assertThat(participateResponse.getTitle()).isEqualTo(competition.getName());
        assertThat(participateResponse.getTeamId()).isNotNull();
        assertThat(participateResponse.getTeammates().size()).isEqualTo(3);
        log.info("teammate1: {} {}", participateResponse.getTeammates().get(0).getEmail(), participateResponse.getTeammates().get(0).getInviteStatus());
        log.info("teammate2: {} {}", participateResponse.getTeammates().get(1).getEmail(), participateResponse.getTeammates().get(1).getInviteStatus());
        log.info("teammate3: {} {}", participateResponse.getTeammates().get(2).getEmail(), participateResponse.getTeammates().get(2).getInviteStatus());
    }

    @Test
    void invitableCheck() {
        Member member = memberRepository.findByEmail("aaa@naver.com").orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        MemberCreateRequestDto memberCreateDto1 = new MemberCreateRequestDto("a@a.com", "회원1", "123");
        MemberCreateRequestDto memberCreateDto2 = new MemberCreateRequestDto("b@b.com", "회원2", "123");
        MemberCreateRequestDto memberCreateDto3 = new MemberCreateRequestDto("c@c.com", "회원3", "123");
        MemberCreateRequestDto memberCreateDto4 = new MemberCreateRequestDto("d@d.com", "회원4", "123");
        memberService.createUser(memberCreateDto1);
        memberService.createUser(memberCreateDto2);
        memberService.createUser(memberCreateDto3);
        memberService.createUser(memberCreateDto4);
        Member loginMember = memberRepository.findByEmail("a@a.com").get();

        CompetitionCreateRequestDto createRequestDto = new CompetitionCreateRequestDto("대회 이름", "대회 설명", LocalDateTime.now(), LocalDateTime.now());
        Competition competition = competitionService.createCompetition(createRequestDto, member.getId());

        TeammateRequestDto teammateRequest1 = new TeammateRequestDto("b@b.com", "회원2");
        TeammateRequestDto teammateRequest2 = new TeammateRequestDto("c@c.com", "회원3");
        List<TeammateRequestDto> teammateRequestList = Arrays.asList(teammateRequest1, teammateRequest2);
        CompetitionParticipateRequestDto competitionParticipateRequest = new CompetitionParticipateRequestDto("팀이름1", teammateRequestList);

        em.flush();
        em.clear();

        CompetitionParticipateResponseDto participateResponse = competitionService.participateInCompetition(competition.getId(), competitionParticipateRequest, loginMember.getId());


        InvitableResponseDto invitableNotMember = competitionService.invitableCheck(competition.getId(), "no@no.com");
        InvitableResponseDto invitableTrue = competitionService.invitableCheck(competition.getId(), "d@d.com");
        InvitableResponseDto invitableFalse = competitionService.invitableCheck(competition.getId(), "c@c.com");


        assertThat(invitableNotMember.isExist()).isFalse();
        assertThat(invitableNotMember.isInvitable()).isFalse();

        assertThat(invitableTrue.isExist()).isTrue();
        assertThat(invitableTrue.isInvitable()).isTrue();

        assertThat(invitableFalse.isExist()).isTrue();
        assertThat(invitableFalse.isInvitable()).isFalse();
    }
}