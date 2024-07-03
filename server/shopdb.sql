DROP TABLE IF EXISTS product_picture;
DROP TABLE IF EXISTS created_product;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS picture;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    user_id int GENERATED ALWAYS AS IDENTITY,
    username text NOT NULL,
    password text NOT NULL,
    balance int NOT NULL,

    PRIMARY KEY(user_id),
    CHECK(balance >= 0)
);

CREATE TABLE product (
    product_id int GENERATED ALWAYS AS IDENTITY,
    name text NOT NULL,
    description text,
    price numeric(15, 2) NOT NULL,
    amount int NOT NULL,

    PRIMARY KEY(product_id),
    CHECK(price >= 0),
    CHECK(amount >= 0)
);

CREATE TABLE picture (
    picture_id int GENERATED ALWAYS AS IDENTITY,
    image bytea NOT NULL,

    PRIMARY KEY(picture_id)
);

CREATE TABLE product_picture (
    pr_id int REFERENCES product(product_id),
    pi_id int REFERENCES picture(picture_id)
);

CREATE TABLE created_product (
    user_id int REFERENCES "user"(user_id),
    pr_id int REFERENCES product(product_id)
);

CREATE TABLE cart (
    user_id int REFERENCES "user"(user_id),
    pr_id int REFERENCES product(product_id)
);