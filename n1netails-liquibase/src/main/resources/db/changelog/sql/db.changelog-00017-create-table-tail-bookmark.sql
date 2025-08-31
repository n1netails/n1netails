CREATE SEQUENCE ntail.tail_bookmark_seq;

CREATE TABLE ntail.tail_bookmark (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    tail_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_tail_bookmark PRIMARY KEY (id),
    CONSTRAINT fk_tail_bookmark_user FOREIGN KEY (user_id) REFERENCES ntail.users(id),
    CONSTRAINT fk_tail_bookmark_tail FOREIGN KEY (tail_id) REFERENCES ntail.tail(id),
    CONSTRAINT uq_user_tail UNIQUE (user_id, tail_id)
);
