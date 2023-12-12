package fluffysnow.idearly.member.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberCreateRequestDto {

    private String email;

    private String name;

    private String password;

}
