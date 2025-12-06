package com.epam.rd.autocode.spring.project.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@RequiredArgsConstructor
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customize(defaultResolver.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientId) {
        return customize(defaultResolver.resolve(request, clientId), request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req, HttpServletRequest request) {
        if (req == null) return null;


        String role = request.getParameter("role");
        if (role == null || role.isEmpty()) {
            role = "CLIENT";
        }


        HttpSession session = request.getSession();
        session.setAttribute("OAUTH2_USER_ROLE", role.toUpperCase());

        return OAuth2AuthorizationRequest.from(req)
                .state(role.toUpperCase())
                .build();
    }
}
