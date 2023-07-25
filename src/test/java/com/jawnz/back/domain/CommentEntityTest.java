package com.jawnz.back.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jawnz.back.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommentEntity.class);
        CommentEntity commentEntity1 = new CommentEntity();
        commentEntity1.setId("id1");
        CommentEntity commentEntity2 = new CommentEntity();
        commentEntity2.setId(commentEntity1.getId());
        assertThat(commentEntity1).isEqualTo(commentEntity2);
        commentEntity2.setId("id2");
        assertThat(commentEntity1).isNotEqualTo(commentEntity2);
        commentEntity1.setId(null);
        assertThat(commentEntity1).isNotEqualTo(commentEntity2);
    }
}
