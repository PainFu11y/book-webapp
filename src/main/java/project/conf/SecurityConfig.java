package project.conf;

import com.epam.rd.autocode.spring.project.enums.UserRole;
import com.epam.rd.autocode.spring.project.security.CustomAuthorizationRequestResolver;
import com.epam.rd.autocode.spring.project.security.JwtFilter;
import com.epam.rd.autocode.spring.project.service.impl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository repo) {
        return new CustomAuthorizationRequestResolver(
                new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization")
        );
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtAuthenticationFilter,
                                           OAuth2AuthorizationRequestResolver resolver) throws Exception {;


        return http
                .csrf(csrf->csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/swagger-ui.html/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/clients").permitAll()
                        .requestMatchers(HttpMethod.POST, "/employees").permitAll()


                        // ADMIN
                        .requestMatchers(
                                "/"
                        ).hasRole(UserRole.ADMIN.name())

                        // USER
                        //TODO Edit personal information and view user profile.
                        .requestMatchers("/books", "/books/{name}").authenticated()

                        // EMPLOYEE
                        //TODO
                        // Confirm orders placed by customers.
                        // Block or unblock customer accounts.
                        // Access a list of registered customers.
                        .requestMatchers(
                                "/employees/**",
                                "/orders/**",
                                "/books/**"
                        ).hasRole(UserRole.EMPLOYEE.name())


                        // CLIENT
                        //TODO
                        // Add books to the basket for purchase.
                        // Delete their account.
                        // Submit orders for purchase.
                        .requestMatchers(
                                "/orders/**",
                                "/clients/**"
                                ).hasRole(UserRole.CLIENT.name())

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(a -> a
                                .authorizationRequestResolver(resolver)
                        )
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler((req, res, auth) -> res.sendRedirect("/oauth2/success"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
