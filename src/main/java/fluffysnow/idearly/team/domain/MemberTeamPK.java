package fluffysnow.idearly.team.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberTeamPK implements Serializable {

    private Long member;
    private Long team;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberTeamPK that = (MemberTeamPK) o;
        return Objects.equals(getMember(), that.getMember()) && Objects.equals(getTeam(), that.getTeam());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMember(), getTeam());
    }
}
