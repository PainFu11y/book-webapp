package java.com.epam.rd.autocode.spring.project.dto;


import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Schema(example = "example@email.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(example = "Password123!")
    private String password;

    @NotBlank(message = "Name cannot be blank")
    @Schema(example = "John Doe")
    private String name;

    @NotBlank(message = "Phone cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    @Schema(example = "+1234567890")
    private String phone;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    @Schema(example = "1985-06-15")
    private LocalDate birthDate;

}