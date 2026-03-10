package com.petcare.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts every HTTP request and validates the Firebase Bearer token.
 * On success: sets UsernamePasswordAuthenticationToken in the SecurityContext.
 * On failure: clears context and continues (SecurityConfig blocks protected
 * routes).
 */
@Slf4j
@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            String role = resolveRole(decodedToken);

            UserPrincipal principal = UserPrincipal.builder()
                    .uid(decodedToken.getUid())
                    .email(decodedToken.getEmail())
                    .name(decodedToken.getName())
                    .role(role)
                    .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated Firebase user: {}", decodedToken.getUid());

        } catch (FirebaseAuthException e) {
            log.warn("⚠️  Invalid Firebase token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts role from Firebase custom claims.
     * Falls back to ROLE_PET_OWNER if no role claim is present.
     */
    private String resolveRole(FirebaseToken token) {
        Object roleClaim = token.getClaims().get("role");
        if (roleClaim instanceof String roleStr && !roleStr.isBlank()) {
            return roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;
        }
        return "ROLE_PET_OWNER";
    }
}
