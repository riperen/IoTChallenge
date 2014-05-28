prompt >> Creation of table EMPLOYEES
create sequence EMPLOYEES_SEQ
  MINVALUE 101
  START WITH 101
  INCREMENT BY 1
  NOCACHE;

create table EMPLOYEES
(
  EMPLOYEE_ID			number				not null,
  FIRST_NAME			varchar2(30 char)	not null,
  MIDDLE_NAME			varchar2(10 char)			,
  LAST_NAME				varchar2(50 char)	not null,
  JOB_TITLE				varchar2(50 char)	not null,
  EXPERTISE				varchar2)255 char)
)
/

ALTER TABLE EMPLOYEES 
ADD CONSTRAINT EMPL_PK PRIMARY KEY ( EMPLOYEE_ID );

CREATE TRIGGER EMPLOYEE_BIS_TRG
  BEFORE INSERT ON EMPLOYEES
  FOR EACH ROW
DECLARE
BEGIN
  IF( :new.EMPLOYEE_ID IS NULL )
  THEN
    :new.EMPLOYEE_ID := EMPLOYEES_SEQ.nextval;
  END IF;
END;