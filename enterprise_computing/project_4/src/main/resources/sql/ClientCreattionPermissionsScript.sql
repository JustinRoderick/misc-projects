# Client creation and permission assignment script for CNT 4714 Project 4 - Spring 2025
#
# IMPORTANT NOTE: Run all database creation scripts before running this script.
#
# Delete the client1, client2,  theaccountant, and project3app users created for Project 3
#  NOTE: If any of these users are currently connected to the MySQL server, terminate their connections prior to running this script.
#  If any of these clients are currently connected, the drop user command will not take effect until the that user's connection is terminated.
# Revoke all privileges, grant option from "client1";
drop user if exists "client1";
# Revoke all privileges, grant option from "client2";
drop user if exists "client2";
# Revoke all privileges, grant option from "project3app";
drop user if exists "project3app";
#Revoke all privileges, grant option from "theaccountant";
drop user if exists "theaccountant";

# Create the client users for Project 4
create user if not exists "client" identified with sha256_password by "client";
create user if not exists "dataentry" identified with sha256_password by "dataentry";
create user if not exists "systemapp" identified with sha256_password by "systemapp";
create user if not exists "theaccountant" identified with sha256_password by "theaccountant";


# Assign privileges to the client users for Project 4
grant select on project4.* to client;
grant select, insert, update on project4.* to dataentry;
grant execute  on project4.* to theaccountant;
grant all on credentialsDB.* to systemapp;

