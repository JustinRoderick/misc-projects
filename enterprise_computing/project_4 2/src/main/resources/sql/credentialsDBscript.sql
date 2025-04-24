# SQL commands to create and populate the credentialsDB database for Project Four
# CNT 4714 - Spring 2025
#
# delete the database if it already exists
drop database if exists credentialsDB;

# create a new database named credentials DB
create database credentialsDB;

# switch to the new database
use credentialsDB;

# create the schemas for the userCredentials relation in this database
create table usercredentials (
    login_username varchar(25),
    login_password varchar(25),
	primary key (login_username)
);

# insert the credentials
insert into usercredentials values ("root", "mysecretpassword");
insert into usercredentials values ("client", "clientpass");
insert into usercredentials values ("dataentry", "dataentrypass");
insert into usercredentials values ("theaccountant", "accountantpass");

# uncomment the following line if you want to see the results of creating database
# select * from usercredentials;
