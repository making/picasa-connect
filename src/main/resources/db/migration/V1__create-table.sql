CREATE TABLE genre (
  genre_id   INTEGER      NOT NULL,
  genre_name VARCHAR(255) NOT NULL,
  user_id    VARCHAR(255) NOT NULL,
  PRIMARY KEY (genre_id)
);
CREATE TABLE photo (
  photo_id  INTEGER      NOT NULL,
  comment   VARCHAR(255) NOT NULL,
  published TIMESTAMP    NOT NULL,
  title     VARCHAR(100) NOT NULL,
  updated   TIMESTAMP    NOT NULL,
  url       VARCHAR(255) NOT NULL,
  user_id   VARCHAR(255) NOT NULL,
  version   INTEGER      NOT NULL,
  genre_id  INTEGER      NOT NULL,
  PRIMARY KEY (photo_id)
);
ALTER TABLE genre ADD CONSTRAINT UK_406iec0ed00mau375ycx41xye UNIQUE (genre_name, user_id);
CREATE INDEX UK_g6e98xfws8xb7ak7va7ihxm27 ON genre (user_id);
ALTER TABLE photo ADD CONSTRAINT UK_kigvc1s15qmrka7nuw4nfpgnk UNIQUE (url);
CREATE INDEX UK_hd8tpnuep2a8ert8ue5u4sgir ON photo (user_id);
CREATE INDEX UK_nmfcxahtma1md5qddcpwg12hv ON photo (genre_id, user_id);
ALTER TABLE photo ADD CONSTRAINT FK_r9jqrlnqt8dgs6nn56x3jvi8k FOREIGN KEY (genre_id) REFERENCES genre;
CREATE SEQUENCE hibernate_sequence;