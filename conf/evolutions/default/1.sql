# --- !Ups
-- table declarations :
create table locations (
    latitude double not null,
    longitude double not null,
    id INTEGER not null primary key autoincrement
  );
create table addresses (
    typeId INTEGER not null,
    id INTEGER not null primary key autoincrement,
    locationId INTEGER not null
  );
create table urban_addresses (
    flatNumber INTEGER,
    houseNumber varchar(128) not null,
    city varchar(128) not null,
    state varchar(128) not null,
    country varchar(128) not null,
    id INTEGER not null primary key,
    street varchar(128) not null
  );
create table countryside_addresses (
    houseNumber varchar(128) not null,
    state varchar(128) not null,
    country varchar(128) not null,
    county varchar(128) not null,
    id INTEGER not null primary key
  );

# --- !Downs
drop table locations;
drop table addresses;
drop table urban_addresses;
drop table countryside_addresses;

-- The drop statements have been generated only for tables.
-- Remember to add appropriate drop statement for objects of different kinds (e.g. sequences).
