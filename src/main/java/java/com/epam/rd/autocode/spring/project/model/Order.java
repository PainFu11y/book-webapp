package java.com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Client client;

    @ManyToOne(optional = false)
    private Employee employee;

    private LocalDateTime orderDate;

    private BigDecimal price;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<BookItem> bookItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}