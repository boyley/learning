DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id       INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(40),
  `name`     VARCHAR(20),
  age      INT(3),
  balance  DECIMAL(10, 2)
);