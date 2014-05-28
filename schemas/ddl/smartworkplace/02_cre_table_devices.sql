prompt >> Creation of table DEVICES
create table DEVICES
(
  DEVICE_ID				varchar2(50 char)	not null,
  DEVICE_NAME			varchar2(100 char)	not null,
  DEVICE_TYPE			varchar2(7 char) 	not null,
  EMPLOYEE_ID			number				not null
)
/

ALTER TABLE DEVICES 
ADD CONSTRAINT DEVI_PK PRIMARY KEY ( DEVICE_ID ) ;
	
ALTER TABLE DEVICES
ADD CONSTRAINT DEVICE_TYPE_CHK
	CHECK (DEVICE_TYPE IN ('Laptop', 'Desktop'));
	
ALTER TABLE DEVICES
ADD CONSTRAINT DEVI_EMPL_FK
	FOREIGN KEY (EMPLOYEE_ID)
	REFERENCES EMPLOYEES (EMPLOYEE_ID);