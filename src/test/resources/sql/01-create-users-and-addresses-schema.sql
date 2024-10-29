CREATE TABLE addresses
(
    id     INT          NOT NULL PRIMARY KEY,
    street VARCHAR(100) NOT NULL,
    city   VARCHAR(100),
    pin    INT
);

CREATE TABLE users
(
    id      INT         NOT NULL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    email   VARCHAR(100),
    phone   INT,
    address INT         NOT NULL,
    CONSTRAINT users_fk FOREIGN KEY (address) REFERENCES addresses (id)
);
