CREATE TABLE ${schemaname}.SIMPLEENTITYOLGH21204 (ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL, NAME VARCHAR(255), PRICE FLOAT, PRIMARY KEY (ID));

CREATE TABLE ${schemaname}.SIMPLEENTITYOLGH21204_AUDIT (AUDIT_ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, ENTITY_ID BIGINT NOT NULL, NAME VARCHAR(255), PRICE FLOAT, PRIMARY KEY (AUDIT_ID));
CREATE TRIGGER ${schemaname}.SIMPLEENTITYOLGH21204_TRIGGER AFTER INSERT ON ${schemaname}.SIMPLEENTITYOLGH21204 REFERENCING NEW_TABLE AS NEW_ENTITY FOR EACH STATEMENT MODE DB2SQL INSERT INTO ${schemaname}.SIMPLEENTITYOLGH21204_AUDIT( ENTITY_ID, NAME, PRICE ) SELECT i.ID, i.NAME, i.PRICE FROM NEW_ENTITY i;