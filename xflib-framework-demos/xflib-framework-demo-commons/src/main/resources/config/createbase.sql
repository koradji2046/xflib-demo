create table T_USER (
	ID varchar(14) not null primary key,
	CODE varchar(20) not null,
	NAME varchar(20),
	SEX varchar(1) default 'x',
	BIRTHDAY date
);

create table T_PROJECT (
	ID varchar(14) not null primary key,
	CODE varchar(20) not null,
	NAME varchar(60),
	DESCRIPTION VARCHAR(100)
);