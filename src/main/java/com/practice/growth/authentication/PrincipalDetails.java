package com.practice.growth.authentication;

// Security가 /login-process 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
// Object => Authentication => User정보가 있어야 됨.
// User Object = UserDetails 객체

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.YNType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 즉, Security Session => Authentication => UserDetails
public class PrincipalDetails implements UserDetails {

    private Account account;

    public PrincipalDetails(Account account) {
        this.account = account;
    }

    // 해당유저의 권한을 Return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> account.getRole());

        return collection;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
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
        return account.getActiveYn().equals(YNType.Y) && account.getDormantYn().equals(YNType.N);
    }
}
