package fluffysnow.idearly.member.dto;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class MemberDto {

    private String email;

    private String name;

    private String password;

    private Role role;

    @Builder
    public MemberDto(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

}
