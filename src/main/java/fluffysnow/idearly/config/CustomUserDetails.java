package fluffysnow.idearly.config;

import fluffysnow.idearly.common.Role;
import fluffysnow.idearly.member.dto.MemberRequestDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private Long memberId;
    private String email;
    private String name;
    private String password;
    private Collection<GrantedAuthority> authorities = new ArrayList<>();


    public CustomUserDetails(Long memberId, String email, String name, String password, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities.add((GrantedAuthority) role::toString);
    }

    //일반 로그인
//    public CustomUserDetails(MemberRequestDto memberRequestDto) {
//        this.memberRequestDto = memberRequestDto;
//    }

//    public MemberRequestDto getMember() {
//        return memberRequestDto;
//    }

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}