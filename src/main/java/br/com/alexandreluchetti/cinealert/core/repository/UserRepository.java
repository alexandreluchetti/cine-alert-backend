package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.User;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.UserRepositoryImpl;

import java.util.Optional;

public interface UserRepository extends UserRepositoryImpl {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
