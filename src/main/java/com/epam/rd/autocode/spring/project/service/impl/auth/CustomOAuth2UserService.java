package com.epam.rd.autocode.spring.project.service.impl.auth;

import com.epam.rd.autocode.spring.project.model.Admin;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");


        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String requestedRole = Optional.ofNullable((String) request.getSession().getAttribute("OAUTH2_USER_ROLE"))
                .orElse("CLIENT");



        String actualRole;

        if (employeeRepository.existsByEmail(email)) actualRole = "EMPLOYEE";
        else if (clientRepository.existsByEmail(email)) actualRole = "CLIENT";
        else if (adminRepository.existsByEmail(email)) actualRole = "ADMIN";
        else {
            switch (requestedRole) {
                case "EMPLOYEE" -> {
                    Employee e = new Employee();
                    e.setEmail(email);
                    e.setName(name);
                    e.setPassword("");
                    e.setBirthDate(LocalDate.of(2000, 1, 1));
                    e.setPhone("+000000000000");
                    employeeRepository.save(e);
                    actualRole = "EMPLOYEE";
                }
                case "ADMIN" -> {
                    Admin a = new Admin();
                    a.setEmail(email);
                    a.setPassword("");
                    adminRepository.save(a);
                    actualRole = "ADMIN";
                }
                default -> {
                    Client c = new Client();
                    c.setEmail(email);
                    c.setName(name);
                    c.setBalance(BigDecimal.ZERO);
                    c.setPassword("");
                    clientRepository.save(c);
                    actualRole = "CLIENT";
                }
            }
        }

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + actualRole)),
                oauthUser.getAttributes(),
                "email"
        );
    }
}
