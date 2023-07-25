package com.jawnz.back.service;

import com.jawnz.back.domain.CommentEntity;
import com.jawnz.back.repository.CommentEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CommentEntity}.
 */
@Service
public class CommentEntityService {

    private final Logger log = LoggerFactory.getLogger(CommentEntityService.class);

    private final CommentEntityRepository commentEntityRepository;

    public CommentEntityService(CommentEntityRepository commentEntityRepository) {
        this.commentEntityRepository = commentEntityRepository;
    }

    /**
     * Save a commentEntity.
     *
     * @param commentEntity the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommentEntity> save(CommentEntity commentEntity) {
        log.debug("Request to save CommentEntity : {}", commentEntity);
        return commentEntityRepository.save(commentEntity);
    }

    /**
     * Update a commentEntity.
     *
     * @param commentEntity the entity to save.
     * @return the persisted entity.
     */
    public Mono<CommentEntity> update(CommentEntity commentEntity) {
        log.debug("Request to update CommentEntity : {}", commentEntity);
        return commentEntityRepository.save(commentEntity);
    }

    /**
     * Partially update a commentEntity.
     *
     * @param commentEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CommentEntity> partialUpdate(CommentEntity commentEntity) {
        log.debug("Request to partially update CommentEntity : {}", commentEntity);

        return commentEntityRepository
            .findById(commentEntity.getId())
            .map(existingCommentEntity -> {
                if (commentEntity.getUserId() != null) {
                    existingCommentEntity.setUserId(commentEntity.getUserId());
                }
                if (commentEntity.getUserName() != null) {
                    existingCommentEntity.setUserName(commentEntity.getUserName());
                }
                if (commentEntity.getContent() != null) {
                    existingCommentEntity.setContent(commentEntity.getContent());
                }
                if (commentEntity.getCreatedAt() != null) {
                    existingCommentEntity.setCreatedAt(commentEntity.getCreatedAt());
                }

                return existingCommentEntity;
            })
            .flatMap(commentEntityRepository::save);
    }

    /**
     * Get all the commentEntities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CommentEntity> findAll(Pageable pageable) {
        log.debug("Request to get all CommentEntities");
        return commentEntityRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of commentEntities available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return commentEntityRepository.count();
    }

    /**
     * Get one commentEntity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Mono<CommentEntity> findOne(String id) {
        log.debug("Request to get CommentEntity : {}", id);
        return commentEntityRepository.findById(id);
    }

    /**
     * Delete the commentEntity by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(String id) {
        log.debug("Request to delete CommentEntity : {}", id);
        return commentEntityRepository.deleteById(id);
    }

    /**
     * Search for the commentEntity corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Flux<CommentEntity> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CommentEntities for query {}", query);
        return commentEntityRepository.search(query, pageable);
    }
}
