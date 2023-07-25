package com.jawnz.back.web.rest;

import com.jawnz.back.domain.CommentEntity;
import com.jawnz.back.repository.CommentEntityRepository;
import com.jawnz.back.service.CommentEntityService;
import com.jawnz.back.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.jawnz.back.domain.CommentEntity}.
 */
@RestController
@RequestMapping("/api")
public class CommentEntityResource {

    private final Logger log = LoggerFactory.getLogger(CommentEntityResource.class);

    private static final String ENTITY_NAME = "jawnzbackCommentEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommentEntityService commentEntityService;

    private final CommentEntityRepository commentEntityRepository;

    public CommentEntityResource(CommentEntityService commentEntityService, CommentEntityRepository commentEntityRepository) {
        this.commentEntityService = commentEntityService;
        this.commentEntityRepository = commentEntityRepository;
    }

    /**
     * {@code POST  /comment-entities} : Create a new commentEntity.
     *
     * @param commentEntity the commentEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commentEntity, or with status {@code 400 (Bad Request)} if the commentEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/comment-entities")
    public Mono<ResponseEntity<CommentEntity>> createCommentEntity(@Valid @RequestBody CommentEntity commentEntity)
        throws URISyntaxException {
        log.debug("REST request to save CommentEntity : {}", commentEntity);
        if (commentEntity.getId() != null) {
            throw new BadRequestAlertException("A new commentEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return commentEntityService
            .save(commentEntity)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/comment-entities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /comment-entities/:id} : Updates an existing commentEntity.
     *
     * @param id the id of the commentEntity to save.
     * @param commentEntity the commentEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentEntity,
     * or with status {@code 400 (Bad Request)} if the commentEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commentEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/comment-entities/{id}")
    public Mono<ResponseEntity<CommentEntity>> updateCommentEntity(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody CommentEntity commentEntity
    ) throws URISyntaxException {
        log.debug("REST request to update CommentEntity : {}, {}", id, commentEntity);
        if (commentEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commentEntityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return commentEntityService
                    .update(commentEntity)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /comment-entities/:id} : Partial updates given fields of an existing commentEntity, field will ignore if it is null
     *
     * @param id the id of the commentEntity to save.
     * @param commentEntity the commentEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentEntity,
     * or with status {@code 400 (Bad Request)} if the commentEntity is not valid,
     * or with status {@code 404 (Not Found)} if the commentEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the commentEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/comment-entities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CommentEntity>> partialUpdateCommentEntity(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody CommentEntity commentEntity
    ) throws URISyntaxException {
        log.debug("REST request to partial update CommentEntity partially : {}, {}", id, commentEntity);
        if (commentEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commentEntityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CommentEntity> result = commentEntityService.partialUpdate(commentEntity);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /comment-entities} : get all the commentEntities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commentEntities in body.
     */
    @GetMapping("/comment-entities")
    public Mono<ResponseEntity<List<CommentEntity>>> getAllCommentEntities(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of CommentEntities");
        return commentEntityService
            .countAll()
            .zipWith(commentEntityService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /comment-entities/:id} : get the "id" commentEntity.
     *
     * @param id the id of the commentEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commentEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/comment-entities/{id}")
    public Mono<ResponseEntity<CommentEntity>> getCommentEntity(@PathVariable String id) {
        log.debug("REST request to get CommentEntity : {}", id);
        Mono<CommentEntity> commentEntity = commentEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commentEntity);
    }

    /**
     * {@code DELETE  /comment-entities/:id} : delete the "id" commentEntity.
     *
     * @param id the id of the commentEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/comment-entities/{id}")
    public Mono<ResponseEntity<Void>> deleteCommentEntity(@PathVariable String id) {
        log.debug("REST request to delete CommentEntity : {}", id);
        return commentEntityService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
                )
            );
    }

    /**
     * {@code SEARCH  /_search/comment-entities?query=:query} : search for the commentEntity corresponding
     * to the query.
     *
     * @param query the query of the commentEntity search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/comment-entities")
    public Mono<ResponseEntity<Flux<CommentEntity>>> searchCommentEntities(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of CommentEntities for query {}", query);
        return commentEntityService
            .countAll()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(commentEntityService.search(query, pageable)));
    }
}
