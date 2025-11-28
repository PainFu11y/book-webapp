package project.controller;

import com.epam.rd.autocode.spring.project.dto.AdminDTO;
import com.epam.rd.autocode.spring.project.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public List<AdminDTO> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{email}")
    public AdminDTO getAdmin(@PathVariable String email) {
        return adminService.getAdminByEmail(email);
    }

    @PostMapping
    public ResponseEntity<AdminDTO> addAdmin(@Valid @RequestBody AdminDTO dto) {
        return ResponseEntity.ok(adminService.addAdmin(dto));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String email) {
        adminService.deleteAdminByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
