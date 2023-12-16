package fluffysnow.idearly.competition.dto;

import fluffysnow.idearly.competition.domain.Competition;
import fluffysnow.idearly.member.domain.Member;
import fluffysnow.idearly.team.domain.MemberTeam;
import fluffysnow.idearly.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class InvitableResponseDto {

    private boolean exist;
    private String memberName;
    private String email;
    private boolean invitable;

    public static InvitableResponseDto of(Member member, boolean invitable) {

        if (member == null) {
            return new InvitableResponseDto(true, null, null, invitable);
        }

        return new InvitableResponseDto(true, member.getName(), member.getEmail(), invitable);
    }
}
