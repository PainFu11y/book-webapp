package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.service.impl.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isOAuth2Path(request)) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String role = null;

        if (isBearerAuth(authHeader)) {
            token = authHeader.substring(7);
            username = jwtUtils.getUsernameFromToken(token);
            role = jwtUtils.getRoleFromToken(token);
        }

        if (isUnauthenticatedWithCredentials(username,role)) {
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(username, role);

            if (jwtUtils.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOAuth2Path(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.startsWith("/auth/login")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console");
    }

    private boolean isBearerAuth(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private boolean isUnauthenticatedWithCredentials(String username, String role) {
        return username != null
                && role != null
                && SecurityContextHolder.getContext().getAuthentication() == null;
    }


}
