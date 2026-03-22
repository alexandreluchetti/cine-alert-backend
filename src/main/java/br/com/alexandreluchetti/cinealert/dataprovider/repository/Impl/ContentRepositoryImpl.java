package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.repository.ContentRepository;
import br.com.alexandreluchetti.cinealert.dataprovider.entity.ContentEntity;
import br.com.alexandreluchetti.cinealert.dataprovider.repository.ContentMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContentRepositoryImpl implements ContentRepository {

    private final ContentMongoRepository contentMongoRepository;

    public ContentRepositoryImpl(ContentMongoRepository contentMongoRepository) {
        this.contentMongoRepository = contentMongoRepository;
    }

    @Override
    public Optional<ContentEntity> findByImdbId(String imdbId) {
        return contentMongoRepository.findByImdbId(imdbId);
    }

    @Override
    public boolean existsByImdbId(String imdbId) {
        return contentMongoRepository.existsByImdbId(imdbId);
    }

    @Override
    public Optional<ContentEntity> findById(String contentId) {
        return contentMongoRepository.findById(contentId);
    }

    @Override
    public ContentEntity save(ContentEntity contentEntity) {
        return contentMongoRepository.save(contentEntity);
    }
}
