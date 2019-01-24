# --- !Ups
-- table declarations :
create table planes (
    id varchar(128) not null primary key,
    seats INTEGER not null
  );
create table locations (
    latitude double not null,
    longitude double not null,
    id INTEGER not null primary key autoincrement
  );
create table addresses (
    id INTEGER not null primary key autoincrement,
    locationID INTEGER not null
  );
create table airports (
    name varchar(128) not null,
    id INTEGER not null primary key autoincrement
  );
-- foreign key constraints :
alter table addresses add constraint addressesFK1 foreign key (locationID) references locations(id);

# --- !Downs
drop table planes;
drop table locations;
drop table addresses;
drop table airports;

-- The drop statements have been generated only for tables.
-- Remember to add appropriate drop statement for objects of different kinds (e.g. sequences).
