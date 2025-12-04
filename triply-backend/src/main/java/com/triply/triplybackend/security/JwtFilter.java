package com.triply.triplybackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validate(token)) {
                Claims claims = jwtUtil.getClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SimpleGrantedAuthority authority =
                            role != null ? new SimpleGrantedAuthority(role) : null;
                    List<SimpleGrantedAuthority> authorities =
                            authority != null ? Collections.singletonList(authority) : Collections.emptyList();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    email,   // principal = email
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        chain.doFilter(req, res);
    }
}
