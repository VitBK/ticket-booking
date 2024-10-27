package ua.epam.mishchenko.ticketbooking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> getByEmail(String email);

    Page<User> getAllByName(Pageable pageable, String name);

    Boolean existsByEmail(String email);
}
