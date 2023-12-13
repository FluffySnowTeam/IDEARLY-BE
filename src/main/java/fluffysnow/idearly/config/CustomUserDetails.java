package fluffysnow.idearly.config;

import fluffysnow.idearly.member.dto.MemberRequestDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final MemberRequestDto memberRequestDto;

    //일반 로그인
    public CustomUserDetails(MemberRequestDto memberRequestDto) {
        this.memberRequestDto = memberRequestDto;
    }

    public MemberRequestDto getMember() {
        return memberRequestDto;
    }

    public Long getId() {
        return memberRequestDto.getId();
    }

    public String getName() {
        return memberRequestDto.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> role = new ArrayList<>();
        role.add((GrantedAuthority) () -> memberRequestDto.getRole().toString());
        return role;
    }

    @Override
    public String getPassword() {
        return memberRequestDto.getPassword();
    }

    @Override
    public String getUsername() {
        return memberRequestDto.getEmail();
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
