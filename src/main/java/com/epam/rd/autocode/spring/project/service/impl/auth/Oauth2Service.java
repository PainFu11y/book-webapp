package com.epam.rd.autocode.spring.project.service.impl.auth;

import com.epam.rd.autocode.spring.project.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Oauth2Service {
    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private final JwtUtils jwtUtils;


    public ResponseEntity<?> oauthSuccess(OAuth2AuthenticationToken authentication){
        OAuth2User user = authentication.getPrincipal();

        String email = user.getAttribute("email");
        String role = authentication.getAuthorities()
                .iterator().next().getAuthority();

        role = role.replace("ROLE_", "");

        String token = jwtUtils.generateToken(email, role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", role,
                "email", email
        ));
    }

    public void oauth2LoginWithRole(String client, String role,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException{
        request.getSession().setAttribute("OAUTH2_USER_ROLE", role.toUpperCase());

        OAuth2AuthorizationRequest authRequest =
                defaultResolver.resolve(request, client);

        response.sendRedirect(authRequest.getAuthorizationRequestUri());
    }
}
