package fluffysnow.idearly.member.dto;

import fluffysnow.idearly.common.Role;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberRequestDto {

    private Long id;

    private String email;

    private String name;

    private String password;

    private Role role;

}
