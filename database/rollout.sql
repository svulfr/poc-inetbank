DROP SCHEMA IF EXISTS pocibs;

CREATE SCHEMA pocibs;

USE pocibs;

-- table for user accounts containing information about credentials and contact information
CREATE TABLE users (
  id       BIGINT       NOT NULL         AUTO_INCREMENT,
  username VARCHAR(63)  NOT NULL,
  name     VARCHAR(127) NOT NULL,
  email    VARCHAR(63)  NOT NULL         DEFAULT '',
  password VARCHAR(255) NOT NULL, -- stores "salted" password hash
  INDEX (username, password), -- index for matching username/password by Spring Security
  UNIQUE INDEX (username) USING HASH,
  FULLTEXT INDEX (name), -- fulltext index to optimize LIKE '%key%' searches (MySQL 5.6.4+)
  FULLTEXT INDEX (email), -- fulltext index to optimize LIKE '%key%' searches (MySQL 5.6.4+)
  PRIMARY KEY (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

-- table for user roles in the system used by Spring Security
CREATE TABLE users_roles (
  username VARCHAR(63) NOT NULL,
  INDEX (username),
  CONSTRAINT FOREIGN KEY (username) REFERENCES users (username),
  role     VARCHAR(31) NOT NULL,
  UNIQUE INDEX (username, role) USING HASH
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

-- table for currency codes
CREATE TABLE currencies (
  code    INT NOT NULL,
  name    VARCHAR(63) DEFAULT '',
  ui_code VARCHAR(5),
  PRIMARY KEY (code)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

-- table for currency exchange rates
CREATE TABLE currencies_exchange (
  code_from INT    NOT NULL,
  CONSTRAINT FOREIGN KEY (code_from) REFERENCES currencies (code),
  code_to   INT    NOT NULL,
  CONSTRAINT FOREIGN KEY (code_to) REFERENCES currencies (code),
  rate      DOUBLE NOT NULL,
  UNIQUE INDEX (code_from, code_to) USING HASH
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

-- table for bak accounts, since multiple accounts are potentially needed for a user
CREATE TABLE accounts (
  id       BIGINT NOT NULL AUTO_INCREMENT,
  user     BIGINT NOT NULL,
  INDEX (user),
  CONSTRAINT FOREIGN KEY (user) REFERENCES users (id),
  amount   DECIMAL(12, 2),
  currency INT,
  CONSTRAINT FOREIGN KEY (currency) REFERENCES currencies (code),
  PRIMARY KEY (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;

-- table for transaction logs, denormalized to contain full information on amounts
CREATE TABLE accounts_tx (
  id            BIGINT         NOT NULL AUTO_INCREMENT,
  tx_type       TINYINT        NOT NULL,
  tx_amount     DECIMAL(12, 2) NOT NULL,
  tx_currency   INT            NOT NULL,
  CONSTRAINT FOREIGN KEY (tx_currency) REFERENCES currencies (code),
  orig          BIGINT         NULL, -- may be null for deposit transactions
  CONSTRAINT FOREIGN KEY (orig) REFERENCES accounts (id),
  orig_amount   DECIMAL(12, 2) NULL,
  orig_currency INT            NULL, -- denormalization, no FK
  rcpt          BIGINT         NULL, -- may be null for withdraw transactions
  CONSTRAINT FOREIGN KEY (rcpt) REFERENCES accounts (id),
  rcpt_amount   DECIMAL(12, 2) NULL,
  rcpt_currency INT            NULL, -- denormalization, no FK
  tx_date       TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
  state         INT            NOT NULL DEFAULT -1,
  PRIMARY KEY (id)
)
  ENGINE InnoDB
  DEFAULT CHARSET utf8;


-- user
GRANT ALL PRIVILEGES ON `pocibs`.* TO 'pocibs'@'localhost'
IDENTIFIED BY 'pocibs';

-- initial data

INSERT INTO currencies (code, name, ui_code) VALUES
  (840, 'U.S. Dollar', 'USD'),
  (978, 'Euro', 'EUR'),
  (643, 'Russian Ruble', 'RUB');

INSERT INTO currencies_exchange (code_from, code_to, rate) VALUES
  (840, 978, 1 / 1.0646),
  (840, 643, 64.8673),
  (978, 840, 1.0646),
  (978, 643, 69.3886),
  (643, 840, 1 / 64.8673),
  (643, 978, 1 / 69.3886);
