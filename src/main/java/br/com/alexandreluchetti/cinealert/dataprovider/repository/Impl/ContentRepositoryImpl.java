package br.com.alexandreluchetti.cinealert.dataprovider.repository.Impl;

import br.com.alexandreluchetti.cinealert.core.model.content.Content;
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
    public Optional<Content> findByImdbId(String imdbId) {
        return contentMongoRepository.findFirstByImdbId(imdbId).map(ContentEntity::toModel);
    }

    @Override
    public boolean existsByImdbId(String imdbId) {
        return contentMongoRepository.existsByImdbId(imdbId);
    }

    @Override
    public Optional<Content> findById(String contentId) {
        return contentMongoRepository.findById(contentId).map(ContentEntity::toModel);
    }

    @Override
    public Content save(Content content) {
        return contentMongoRepository.save(ContentEntity.fromModel(content)).toModel();
    }
}
