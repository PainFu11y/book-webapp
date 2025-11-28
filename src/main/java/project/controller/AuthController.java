package project.controller;

import com.epam.rd.autocode.spring.project.model.request.AuthRequest;
import com.epam.rd.autocode.spring.project.security.JwtUtils;
import com.epam.rd.autocode.spring.project.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login/client")
    public ResponseEntity<?> loginClient(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "CLIENT");
    }

    @PostMapping("/login/employee")
    public ResponseEntity<?> loginEmployee(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "EMPLOYEE");
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest dto) {
        return authenticate(dto.getEmail(), dto.getPassword(), "ADMIN");
    }




    private ResponseEntity<?> authenticate(String email, String password, String role) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(email, role);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password)
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "role", role
            ));

        } catch (BadCredentialsException ex) {
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
