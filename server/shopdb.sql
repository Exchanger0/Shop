DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    user_id int GENERATED ALWAYS AS IDENTITY,
    username text NOT NULL,
    password text NOT NULL,
    type text NOT NULL,
    balance int NOT NULL,

    PRIMARY KEY(user_id),
    CHECK(type IN ('CONSUMER', 'PRODUCER')),
    CHECK(balance >= 0)
);