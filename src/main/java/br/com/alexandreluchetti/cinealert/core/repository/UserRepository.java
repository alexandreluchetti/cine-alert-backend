package br.com.alexandreluchetti.cinealert.core.repository;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
