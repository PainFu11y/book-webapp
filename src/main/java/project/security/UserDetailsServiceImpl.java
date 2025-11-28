package project.security;

import com.epam.rd.autocode.spring.project.enums.UserRole;
import com.epam.rd.autocode.spring.project.model.Admin;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Client> clientOpt = clientRepository.findByEmail(email);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();

            return User.builder()
                    .username(client.getEmail())
                    .password(client.getPassword())
                    .roles(UserRole.CLIENT.toString())
                    .build();
        }

        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();

            return User.builder()
                    .username(employee.getEmail())
                    .password(employee.getPassword())
                    .roles(UserRole.EMPLOYEE.toString())
                    .build();
        }

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .roles(UserRole.ADMIN.toString())
                    .build();
        }


        throw new UsernameNotFoundException("Username not found by email " + email);
    }

    public UserDetails loadUserByUsernameAndRole(String email, String role) throws UsernameNotFoundException {
        switch (role) {
            case "ROLE_CLIENT":
            case "CLIENT":
                return clientRepository.findByEmail(email)
                        .map(c -> User.builder()
                                .username(c.getEmail())
                                .password(c.getPassword())
                                .roles(UserRole.CLIENT.toString())
                                .build())
                        .orElseThrow(() -> new UsernameNotFoundException("Client not found"));
            case "ROLE_EMPLOYEE":
            case "EMPLOYEE":
                return employeeRepository.findByEmail(email)
                        .map(e -> User.builder()
                                .username(e.getEmail())
                                .password(e.getPassword())
                                .roles(UserRole.EMPLOYEE.toString())
                                .build())
                        .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));
            case "ROLE_ADMIN":
            case "ADMIN":
                return adminRepository.findByEmail(email)
                        .map(a -> User.builder()
                                .username(a.getEmail())
                                .password(a.getPassword())
                                .roles(UserRole.ADMIN.toString())
                                .build())
                        .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
            default:
                throw new UsernameNotFoundException("Unknown role");
        }
    }


}
