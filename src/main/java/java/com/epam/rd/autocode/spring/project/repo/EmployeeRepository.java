package java.com.epam.rd.autocode.spring.project.repo;


import com.epam.rd.autocode.spring.project.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Employee> findByPhone(String phone);

    void deleteByEmail(String email);
}