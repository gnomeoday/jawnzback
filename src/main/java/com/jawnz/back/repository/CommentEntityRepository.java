package com.jawnz.back.repository;

import com.jawnz.back.domain.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the CommentEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentEntityRepository extends ReactiveMongoRepository<CommentEntity, String> {
    Flux<CommentEntity> findAllBy(Pageable pageable);
}
