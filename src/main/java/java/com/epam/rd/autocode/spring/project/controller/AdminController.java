package java.com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.AdminDTO;
import com.epam.rd.autocode.spring.project.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "Get all admins")
    public List<AdminDTO> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get admin by email")
    public AdminDTO getAdmin(@PathVariable String email) {
        return adminService.getAdminByEmail(email);
    }

    @PostMapping
    @Operation(summary = "Add new admin")
    public ResponseEntity<AdminDTO> addAdmin(@Valid @RequestBody AdminDTO dto) {
        return ResponseEntity.ok(adminService.addAdmin(dto));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete admin by email")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String email) {
        adminService.deleteAdminByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
