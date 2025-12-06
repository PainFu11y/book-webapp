package java.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.request.ForgotPasswordRequestDTO;
import com.epam.rd.autocode.spring.project.dto.request.ResetPasswordRequestDTO;
import com.epam.rd.autocode.spring.project.model.request.AuthRequest;
import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.security.UserDetailsServiceImpl;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import com.epam.rd.autocode.spring.project.service.impl.LoginAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordResetService passwordResetService;
    private final LoginAttemptService loginAttemptService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;


    private final RedisTemplate<String, String> redisTemplate;


    @PostMapping("/login/client")
    @Operation(summary = "Login as client")
    public ResponseEntity<?> loginClient(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "CLIENT");
    }

    @PostMapping("/login/employee")
    @Operation(summary = "Login as employee")
    public ResponseEntity<?> loginEmployee(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "EMPLOYEE");
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Login as admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "ADMIN");
    }


    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset token")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO dto) throws MessagingException {
        passwordResetService.sendResetToken(dto);
        return ResponseEntity.ok("Reset token sent to email");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Password has been reset successfully");
    }




    private ResponseEntity<?> authenticate(String email, String password, String role) {
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
