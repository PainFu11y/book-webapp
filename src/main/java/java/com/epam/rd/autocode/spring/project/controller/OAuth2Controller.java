package java.com.epam.rd.autocode.spring.project.controller;


import com.epam.rd.autocode.spring.project.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2AuthorizationRequestResolver defaultResolver;

    private final JwtUtils jwtUtils;

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

    @GetMapping("/oauth2/authorization/{client}/{role}")
    public void oauth2LoginWithRole(@PathVariable String client,
                                    @PathVariable String role,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {

        request.getSession().setAttribute("OAUTH2_USER_ROLE", role.toUpperCase());


        OAuth2AuthorizationRequest authRequest =
                defaultResolver.resolve(request, client);

        response.sendRedirect(authRequest.getAuthorizationRequestUri());
    }


}
