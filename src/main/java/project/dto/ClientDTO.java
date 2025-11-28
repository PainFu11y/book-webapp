package project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

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

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be zero or positive")
    private BigDecimal balance;

}