package com.epam.rd.autocode.spring.project.service.impl.auth;

import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.service.impl.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;


    public ResponseEntity<?> authenticate(String email, String password, String role) {
        try {
            if (loginAttemptService.isBlocked(email)) {
                return ResponseEntity.status(423).body(Map.of(
                        "error", "Account is locked due to too many failed attempts",
                        "retry_after_seconds", loginAttemptService.getRemainingBlockTime(email)
                ));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(email, role);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password)
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(userDetails);

            loginAttemptService.resetAttempts(email);
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "role", role
            ));

        }catch (UsernameNotFoundException ex){
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found"
            ));
        }
        catch (BadCredentialsException ex) {
            loginAttemptService.increaseFailedAttempts(email);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Invalid email or password"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Authentication failed",
                    "details", ex.getMessage()
            ));
        }
    }
}
