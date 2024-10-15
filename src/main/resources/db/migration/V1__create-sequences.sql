CREATE SEQUENCE docnumber_seq NOCACHE;
CREATE SEQUENCE inscription_code_seq NOCACHE;
CREATE SEQUENCE classroom_seq NOCACHE;
CREATE TABLE app_user (
    uuid VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    cpf VARCHAR(14) NOT NULL
);

INSERT INTO app_user (uuid, username, password, role, cpf) VALUES ('e07863ce-83cb-41bb-a9c0-f378c305a8dc', 'admin', '$2a$10$/XM7wfeHZD2gDqJ4/VuJQejsWG3eLkYa6CfIlh1.SAsNEFSfyzEhO', 0, '000.000.000-00');
