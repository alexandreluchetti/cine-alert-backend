package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.user.User;
import br.com.alexandreluchetti.cinealert.core.repository.UserRepository;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.UserEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.UserMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMongoRepository userMongoRepository;

    public UserRepositoryImpl(UserMongoRepository userMongoRepository) {
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email).map(UserEntity::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userMongoRepository.save(UserEntity.fromModel(user)).toModel();
    }
}
