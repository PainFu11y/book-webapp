package java.com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class Admin extends User {
}
