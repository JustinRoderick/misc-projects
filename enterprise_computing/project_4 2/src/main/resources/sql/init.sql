-- Create credentialsDB
DROP DATABASE IF EXISTS credentialsDB;
CREATE DATABASE credentialsDB;

-- Create users table in credentialsDB
USE credentialsDB;
CREATE TABLE IF NOT EXISTS usercredentials (
    login_username varchar(25),
    login_password varchar(25),
    PRIMARY KEY (login_username)
);

-- Insert user credentials
INSERT INTO usercredentials (login_username, login_password) VALUES
('root', 'mysecretpassword'),
('client', 'clientpass'),
('dataentry', 'dataentrypass'),
('theaccountant', 'accountantpass');

-- Create project4 database if it doesn't exist
CREATE DATABASE IF NOT EXISTS project4;

-- Drop and recreate systemapp user with mysql_native_password
DROP USER IF EXISTS 'systemapp'@'%';
CREATE USER 'systemapp'@'%' IDENTIFIED WITH mysql_native_password BY 'systempass';

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON credentialsDB.* TO 'systemapp'@'%';
GRANT ALL PRIVILEGES ON project4.* TO 'systemapp'@'%';

FLUSH PRIVILEGES;
