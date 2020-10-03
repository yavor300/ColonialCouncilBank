package app.ccb.repositories;

import app.ccb.domain.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    List<Client> findAllByFullName(String fullName);

    @Query(value = "SELECT cl FROM Client cl ORDER BY cl.bankAccount.cards.size DESC")
    List<Client> findAllByMostCards();
}
