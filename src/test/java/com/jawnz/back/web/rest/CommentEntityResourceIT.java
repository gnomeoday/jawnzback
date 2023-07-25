package com.jawnz.back.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.jawnz.back.IntegrationTest;
import com.jawnz.back.domain.CommentEntity;
import com.jawnz.back.repository.CommentEntityRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CommentEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommentEntityResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/comment-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/comment-entities";

    @Autowired
    private CommentEntityRepository commentEntityRepository;

    @Autowired
    private WebTestClient webTestClient;

    private CommentEntity commentEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentEntity createEntity() {
        CommentEntity commentEntity = new CommentEntity()
            .userId(DEFAULT_USER_ID)
            .userName(DEFAULT_USER_NAME)
            .content(DEFAULT_CONTENT)
            .createdAt(DEFAULT_CREATED_AT);
        return commentEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentEntity createUpdatedEntity() {
        CommentEntity commentEntity = new CommentEntity()
            .userId(UPDATED_USER_ID)
            .userName(UPDATED_USER_NAME)
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT);
        return commentEntity;
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        commentEntityRepository.deleteAll().block();
        commentEntity = createEntity();
    }

    @Test
    void createCommentEntity() throws Exception {
        int databaseSizeBeforeCreate = commentEntityRepository.findAll().collectList().block().size();
        // Create the CommentEntity
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeCreate + 1);
        CommentEntity testCommentEntity = commentEntityList.get(commentEntityList.size() - 1);
        assertThat(testCommentEntity.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testCommentEntity.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testCommentEntity.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testCommentEntity.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    void createCommentEntityWithExistingId() throws Exception {
        // Create the CommentEntity with an existing ID
        commentEntity.setId("existing_id");

        int databaseSizeBeforeCreate = commentEntityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentEntityRepository.findAll().collectList().block().size();
        // set the field null
        commentEntity.setUserId(null);

        // Create the CommentEntity, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentEntityRepository.findAll().collectList().block().size();
        // set the field null
        commentEntity.setUserName(null);

        // Create the CommentEntity, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentEntityRepository.findAll().collectList().block().size();
        // set the field null
        commentEntity.setContent(null);

        // Create the CommentEntity, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentEntityRepository.findAll().collectList().block().size();
        // set the field null
        commentEntity.setCreatedAt(null);

        // Create the CommentEntity, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCommentEntities() {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        // Get all the commentEntityList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(commentEntity.getId()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID))
            .jsonPath("$.[*].userName")
            .value(hasItem(DEFAULT_USER_NAME))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getCommentEntity() {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        // Get the commentEntity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, commentEntity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(commentEntity.getId()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID))
            .jsonPath("$.userName")
            .value(is(DEFAULT_USER_NAME))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getNonExistingCommentEntity() {
        // Get the commentEntity
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCommentEntity() throws Exception {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();

        // Update the commentEntity
        CommentEntity updatedCommentEntity = commentEntityRepository.findById(commentEntity.getId()).block();
        updatedCommentEntity.userId(UPDATED_USER_ID).userName(UPDATED_USER_NAME).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCommentEntity.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCommentEntity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
        CommentEntity testCommentEntity = commentEntityList.get(commentEntityList.size() - 1);
        assertThat(testCommentEntity.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testCommentEntity.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testCommentEntity.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testCommentEntity.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    void putNonExistingCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentEntity.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommentEntityWithPatch() throws Exception {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();

        // Update the commentEntity using partial update
        CommentEntity partialUpdatedCommentEntity = new CommentEntity();
        partialUpdatedCommentEntity.setId(commentEntity.getId());

        partialUpdatedCommentEntity.userId(UPDATED_USER_ID).content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommentEntity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentEntity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
        CommentEntity testCommentEntity = commentEntityList.get(commentEntityList.size() - 1);
        assertThat(testCommentEntity.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testCommentEntity.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testCommentEntity.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testCommentEntity.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    void fullUpdateCommentEntityWithPatch() throws Exception {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();

        // Update the commentEntity using partial update
        CommentEntity partialUpdatedCommentEntity = new CommentEntity();
        partialUpdatedCommentEntity.setId(commentEntity.getId());

        partialUpdatedCommentEntity
            .userId(UPDATED_USER_ID)
            .userName(UPDATED_USER_NAME)
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommentEntity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentEntity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
        CommentEntity testCommentEntity = commentEntityList.get(commentEntityList.size() - 1);
        assertThat(testCommentEntity.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testCommentEntity.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testCommentEntity.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testCommentEntity.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    void patchNonExistingCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commentEntity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCommentEntity() throws Exception {
        int databaseSizeBeforeUpdate = commentEntityRepository.findAll().collectList().block().size();
        commentEntity.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentEntity))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CommentEntity in the database
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCommentEntity() {
        // Initialize the database
        commentEntityRepository.save(commentEntity).block();

        int databaseSizeBeforeDelete = commentEntityRepository.findAll().collectList().block().size();

        // Delete the commentEntity
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, commentEntity.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CommentEntity> commentEntityList = commentEntityRepository.findAll().collectList().block();
        assertThat(commentEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    void searchCommentEntity() {
        // Initialize the database
        commentEntity = commentEntityRepository.save(commentEntity).block();

        // Search the commentEntity
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + commentEntity.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(commentEntity.getId()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID))
            .jsonPath("$.[*].userName")
            .value(hasItem(DEFAULT_USER_NAME))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }
}
