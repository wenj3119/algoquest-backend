CREATE TABLE user_problem_progress (
    user_id             BIGINT       NOT NULL REFERENCES users(id),
    problem_id          BIGINT       NOT NULL,
    status              VARCHAR(30)  NOT NULL DEFAULT 'not_started',
    code                TEXT         NOT NULL DEFAULT '',
    step_results        TEXT         NOT NULL DEFAULT '{}',
    current_step_index  INT          NOT NULL DEFAULT 0,
    last_submit_result  TEXT,
    updated_at          TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (user_id, problem_id)
);

CREATE TABLE mistake_records (
    id             VARCHAR(64)  PRIMARY KEY,
    user_id        BIGINT       NOT NULL REFERENCES users(id),
    problem_id     BIGINT       NOT NULL,
    submit_status  VARCHAR(30)  NOT NULL,
    reason         VARCHAR(30)  NOT NULL,
    note           TEXT,
    created_at     TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_mistake_records_user         ON mistake_records(user_id);
CREATE INDEX idx_mistake_records_user_created ON mistake_records(user_id, created_at);

CREATE TABLE hint_usage_records (
    id                   VARCHAR(64)  PRIMARY KEY,
    user_id              BIGINT       NOT NULL REFERENCES users(id),
    problem_id           BIGINT       NOT NULL,
    submit_status        VARCHAR(30)  NOT NULL,
    mistake_reason       VARCHAR(30),
    max_unlocked_level   INT          NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_hint_usage_records_user         ON hint_usage_records(user_id);
CREATE INDEX idx_hint_usage_records_user_created ON hint_usage_records(user_id, created_at);
