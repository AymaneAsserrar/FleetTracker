package com.example.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                if (jwtUtil.isValid(token)) {
                    String username  = jwtUtil.getUsername(token);
                    Boolean isManager = jwtUtil.getIsManager(token);
                    Object rawId     = jwtUtil.parse(token).get("driverId");
                    Long driverId    = rawId instanceof Number n ? n.longValue() : null;

                    String role = Boolean.TRUE.equals(isManager) ? "ROLE_MANAGER" : "ROLE_DRIVER";
                    var auth = new UsernamePasswordAuthenticationToken(
                            username, null,
                            List.of(new SimpleGrantedAuthority(role))
                    );
                    auth.setDetails(driverId);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                // invalid token — proceed as unauthenticated, security layer returns 401
            }
        }

        chain.doFilter(request, response);
    }
}
