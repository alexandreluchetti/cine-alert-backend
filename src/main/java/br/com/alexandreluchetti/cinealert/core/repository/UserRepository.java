package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
