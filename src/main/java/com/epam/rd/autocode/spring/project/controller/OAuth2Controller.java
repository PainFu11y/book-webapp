package com.epam.rd.autocode.spring.project.controller;


import com.epam.rd.autocode.spring.project.service.impl.auth.Oauth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final Oauth2Service oauth2Service;

    @GetMapping("/login/client")
    public void loginClient(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google?role=CLIENT");
    }

    @GetMapping("/login/employee")
    public void loginEmployee(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google?role=EMPLOYEE");
    }

    @GetMapping("/login/admin")
    public void loginAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google?role=ADMIN");
    }

    @GetMapping("/success")
    public ResponseEntity<?> oauthSuccess(OAuth2AuthenticationToken authentication) {
        return oauth2Service.oauthSuccess(authentication);
    }

    @GetMapping("/oauth2/authorization/{client}/{role}")
    public void oauth2LoginWithRole(@PathVariable String client,
                                    @PathVariable String role,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {

           oauth2Service.oauth2LoginWithRole(client, role, request, response);
    }


}
