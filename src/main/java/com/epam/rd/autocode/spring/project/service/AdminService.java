package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    List<AdminDTO> getAllAdmins();
    AdminDTO getAdminByEmail(String email);
    AdminDTO addAdmin(AdminDTO dto);
    void deleteAdminByEmail(String email);

}
