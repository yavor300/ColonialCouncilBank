package app.ccb.repositories;

import app.ccb.domain.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByFirstNameAndLastName(String firstName, String lastName);

    @Query(value = "SELECT e FROM Employee e WHERE e.clients.size > 0 ORDER BY e.clients.size DESC, e.id ASC")
    List<Employee> findAllByClientsIsNotNullOrderByClientsDescIdAsc();
}
