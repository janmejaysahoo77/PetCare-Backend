package com.petcare.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents the currently authenticated Firebase user in the Spring Security
 * context.
 * Populated by FirebaseTokenFilter after token verification.
 */
@Data
@Builder
public class UserPrincipal implements UserDetails {

    private String uid; // Firebase UID
    private String email;
    private String name;
    private String role; // e.g. "ROLE_PET_OWNER", "ROLE_VET", "ROLE_ADMIN"

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role != null ? role : "ROLE_PET_OWNER"));
    }

    @Override
    public String getPassword() {
        return null;
    } // No password — Firebase auth

    @Override
    public String getUsername() {
        return uid;
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
