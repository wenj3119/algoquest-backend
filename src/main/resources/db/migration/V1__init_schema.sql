-- Schema for AlgoQuest Phase 2

CREATE TYPE difficulty_level AS ENUM ('EASY', 'MEDIUM', 'HARD');
CREATE TYPE problem_status AS ENUM ('draft', 'pending_review', 'published', 'rejected');
CREATE TYPE problem_source AS ENUM ('builtin', 'ai_generated', 'user');
CREATE TYPE comparison_strategy AS ENUM ('EXACT', 'TEXT_NORMALIZE', 'UNORDERED', 'FLOAT_TOLERANCE', 'CUSTOM');

CREATE TABLE problems (
    id                 BIGINT           PRIMARY KEY,
    title              VARCHAR(200)     NOT NULL,
    difficulty         difficulty_level NOT NULL,
    category           VARCHAR(100)     NOT NULL,
    description        TEXT             NOT NULL,
    starter_code       TEXT             NOT NULL,
    sort_order         INT              NOT NULL DEFAULT 0,
    status             problem_status   NOT NULL DEFAULT 'published',
    source             problem_source   NOT NULL DEFAULT 'builtin',
    reference_solution TEXT
);

-- Sequence starting at 11 for new problems
CREATE SEQUENCE problems_id_seq START WITH 11 OWNED BY problems.id;

CREATE TABLE problem_examples (
    id          BIGSERIAL PRIMARY KEY,
    problem_id  BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    input       TEXT   NOT NULL,
    output      TEXT   NOT NULL,
    explanation TEXT,
    sort_order  INT    NOT NULL DEFAULT 0
);

CREATE TABLE problem_steps (
    id          BIGSERIAL PRIMARY KEY,
    problem_id  BIGINT       NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    step_key    VARCHAR(50)  NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    type        VARCHAR(50)  NOT NULL DEFAULT 'single_choice',
    answer      VARCHAR(10)  NOT NULL,
    explanation TEXT         NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0
);

CREATE TABLE problem_step_options (
    id         BIGSERIAL PRIMARY KEY,
    step_id    BIGINT      NOT NULL REFERENCES problem_steps(id) ON DELETE CASCADE,
    label      VARCHAR(10) NOT NULL,
    content    TEXT        NOT NULL,
    sort_order INT         NOT NULL DEFAULT 0
);

CREATE TABLE problem_judge_specs (
    id                  BIGSERIAL           PRIMARY KEY,
    problem_id          BIGINT              NOT NULL UNIQUE REFERENCES problems(id) ON DELETE CASCADE,
    method_name         VARCHAR(100)        NOT NULL,
    params              JSONB               NOT NULL DEFAULT '[]',
    return_type         VARCHAR(100)        NOT NULL,
    -- output_target: for void methods, which param name to read as output (replaces positional convention)
    output_target       VARCHAR(100),
    comparison_strategy comparison_strategy NOT NULL DEFAULT 'EXACT',
    -- comparison_options stores strategy-specific config, e.g. {"epsilon": 1e-6} for FLOAT_TOLERANCE
    comparison_options  JSONB,
    time_limit_ms       INT                 NOT NULL DEFAULT 5000,
    memory_limit_mb     INT                 NOT NULL DEFAULT 256
);

CREATE TABLE problem_test_cases (
    id            BIGSERIAL PRIMARY KEY,
    problem_id    BIGINT  NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    display_input TEXT    NOT NULL,
    inputs        JSONB   NOT NULL DEFAULT '[]',
    expected      JSONB   NOT NULL,
    is_sample     BOOLEAN NOT NULL DEFAULT false,
    sort_order    INT     NOT NULL DEFAULT 0
);

CREATE INDEX idx_problem_examples_problem_id ON problem_examples(problem_id, sort_order);
CREATE INDEX idx_problem_steps_problem_id    ON problem_steps(problem_id, sort_order);
CREATE INDEX idx_problem_step_options_step   ON problem_step_options(step_id, sort_order);
CREATE INDEX idx_problem_test_cases_problem  ON problem_test_cases(problem_id, sort_order);
