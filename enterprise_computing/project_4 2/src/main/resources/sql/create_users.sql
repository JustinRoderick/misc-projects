-- Drop users if they exist
DROP USER IF EXISTS 'client'@'%';
DROP USER IF EXISTS 'dataentry'@'%';
DROP USER IF EXISTS 'theaccountant'@'%';

-- Create users with passwords matching properties files
CREATE USER 'client'@'%' IDENTIFIED BY 'clientpass';
CREATE USER 'dataentry'@'%' IDENTIFIED BY 'dataentrypass';
CREATE USER 'theaccountant'@'%' IDENTIFIED BY 'accountantpass';

-- Grant read-only access to client user
GRANT SELECT ON project4.* TO 'client'@'%';

-- Grant data entry permissions
GRANT SELECT, INSERT, UPDATE ON project4.* TO 'dataentry'@'%';

-- Grant accountant permissions
GRANT SELECT ON project4.* TO 'theaccountant'@'%';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;
