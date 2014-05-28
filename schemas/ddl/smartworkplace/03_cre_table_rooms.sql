prompt >> Creation of table ROOMS
create table ROOMS
(
  ROOM_ID				number				not null,
  ROOM_NAME				varchar2(20 char)	not null,
  ROOM_DIMENSIONS		varchar2(100 char)	not null,
  DOOR_LOCATION			varchar2(50 char)	not null
)
/

ALTER TABLE ROOMS 
ADD CONSTRAINT ROOM_PK PRIMARY KEY ( ROOM_ID ) ;