CREATE DATABASE IF NOT EXISTS delivery_db;
CREATE DATABASE IF NOT EXISTS driver_client_db;
CREATE DATABASE IF NOT EXISTS package_db;

CREATE USER IF NOT EXISTS 'delivery_user'@'%' IDENTIFIED BY 'delivery_pass';
CREATE USER IF NOT EXISTS 'driver_client_user'@'%' IDENTIFIED BY 'driver_client_pass';
CREATE USER IF NOT EXISTS 'package_user'@'%' IDENTIFIED BY 'package_pass';

GRANT ALL PRIVILEGES ON delivery_db.* TO 'delivery_user'@'%';
GRANT ALL PRIVILEGES ON driver_client_db.* TO 'driver_client_user'@'%';
GRANT ALL PRIVILEGES ON package_db.* TO 'package_user'@'%';

FLUSH PRIVILEGES;
