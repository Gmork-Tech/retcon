create sequence LocalUser_SEQ start with 1 increment by 50;
create table Application (id uuid not null, name varchar(255) not null, primary key (id));
create table ConfigProp (nullable boolean not null, propType tinyint not null check (propType between 0 and 5), deploymentId bigint, name varchar(255) not null, val json, primary key (name));
create table Deployment (convertToFull boolean, incrementDelay numeric(21,0), incrementPercentage smallint, incrementQuantity smallint, initialPercentage smallint, initialQuantity smallint, shouldIncrement boolean, targetPercentage smallint, targetQuantity smallint, id bigint generated by default as identity, lastDeployed timestamp(6) with time zone, applicationId uuid, kind varchar(31) not null, name varchar(255), targetHosts json, primary key (id));
create table LocalUser (id bigint not null, password varchar(255), role varchar(255), username varchar(255), primary key (id));
alter table if exists ConfigProp add constraint FK4itdvbaaedod3y5a4nl39fm00 foreign key (deploymentId) references Deployment;
alter table if exists Deployment add constraint FKl8r6fcaxtyopae9i6efx4de0b foreign key (applicationId) references Application;