CREATE TABLE data_source_config
(
    id     serial  PRIMARY KEY,
    dataBaseName   VARCHAR(255),
    url            VARCHAR(255),
    driverClassName VARCHAR(255),
    username  VARCHAR(255),
    password  VARCHAR(255),
    isActive  BIT
);