CREATE TABLE data_source_config
(
    id     serial  PRIMARY KEY,
    name   VARCHAR(255),
    value  VARCHAR(255),
    description  VARCHAR(255),
    is_active  BIT
);