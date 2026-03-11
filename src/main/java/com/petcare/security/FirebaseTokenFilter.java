package com.petcare.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Intercepts every HTTP request and validates the Firebase Bearer token.
 *
 * In DEVELOPMENT mode (firebase.verify-token=false), it decodes the JWT payload
 * locally without making any outbound HTTPS calls to Google's certificate endpoint.
 *
 * In PRODUCTION mode (firebase.verify-token=true), it uses FirebaseAuth to fully
 * verify the token signature against Google's public key certificates.
 */
@Slf4j
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final boolean verifyToken;

    public FirebaseTokenFilter(boolean verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip token validation for OPTIONS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            UserPrincipal principal;

            if (verifyToken) {
                // ── PRODUCTION: Full online verification ──────────────────
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String role = resolveRoleFromClaims(decodedToken.getClaims());

                principal = UserPrincipal.builder()
                        .uid(decodedToken.getUid())
                        .email(decodedToken.getEmail())
                        .name(decodedToken.getName())
                        .role(role)
                        .build();

                log.debug("✅ [PROD] Verified Firebase user: {}", decodedToken.getUid());
            } else {
                // ── DEV MODE: Local JWT payload decoding (no network call) ─
                principal = decodeTokenLocally(token);
                log.debug("⚙️  [DEV] Locally decoded Firebase token for user: {}", principal.getUid());
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException e) {
            log.warn("⚠️  Invalid Firebase token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.warn("⚠️  Failed to decode token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Decodes a Firebase JWT payload locally from Base64 — no network call needed.
     * Safe for development use only.
     */
    @SuppressWarnings("unchecked")
    private UserPrincipal decodeTokenLocally(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        // Decode the payload (2nd part) from Base64URL
        byte[] payloadBytes = Base64.getUrlDecoder().decode(addPadding(parts[1]));
        Map<String, Object> claims = OBJECT_MAPPER.readValue(payloadBytes, Map.class);

        String uid = (String) claims.get("user_id");
        if (uid == null) uid = (String) claims.get("sub");

        String email = (String) claims.get("email");
        String name = (String) claims.get("name");

        String role = resolveRoleFromClaims(claims);

        return UserPrincipal.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }

    private String addPadding(String base64) {
        int pad = 4 - base64.length() % 4;
        if (pad < 4) base64 += "=".repeat(pad);
        return base64;
    }

    private String resolveRoleFromClaims(Map<String, Object> claims) {
        Object roleClaim = claims.get("role");
        if (roleClaim instanceof String roleStr && !roleStr.isBlank()) {
            return roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;
        }
        return "ROLE_PET_OWNER";
    }
}
