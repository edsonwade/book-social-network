# PostgreSQL on Ubuntu in WSL2 🚀

This guide will walk you through installing and configuring **PostgreSQL** on **Ubuntu** running within **WSL2** (Windows Subsystem for Linux version 2).

## Install PostgreSQL on Ubuntu (WSL2) 🛠️

```bash
# 1. Update package lists
sudo apt update

# 2. Install PostgreSQL server and client
sudo apt install postgresql postgresql-contrib

# 3. Check if PostgreSQL is running
sudo service postgresql status

# 4. If not running, start the PostgreSQL service
sudo service postgresql start

# 1. Switch to the `postgres` user
sudo -i -u postgres

# 2. Enter PostgreSQL interactive terminal
psql

# 3. Create a new user (Optional)
CREATE USER myuser WITH PASSWORD 'mypassword';

# 4. Create a new database and grant privileges
CREATE DATABASE mydatabase;
GRANT ALL PRIVILEGES ON DATABASE mydatabase TO myuser;

# 5. Exit the PostgreSQL shell
\q
```

# 1. Edit PostgreSQL configuration to allow all IP connections
sudo nano /etc/postgresql/12/main/postgresql.conf

# Find listen_addresses line and set it to '*' (allow all connections)
listen_addresses = '*'

# Save and exit (Ctrl + X, Y, Enter)

# 2. Edit pg_hba.conf to allow all IP addresses
sudo nano /etc/postgresql/12/main/pg_hba.conf

# Add the following line to allow connections from any IP
host    all             all             0.0.0.0/0               md5

# Save and exit (Ctrl + X, Y, Enter)

# 3. Restart PostgreSQL to apply changes
sudo service postgresql restart

## Test PostgreSQL Connection 🔌
# Log into PostgreSQL from the terminal with user and database
psql -U myuser -d mydatabase

## Connect to PostgreSQL from Windows (Optional) 💻
# 1. Find the IP address of your WSL2 instance
ip addr | grep inet

# 2. If needed, update pg_hba.conf to allow Windows access
# Edit /etc/postgresql/12/main/pg_hba.conf and allow connections from Windows IP

# 3. Restart PostgreSQL if changes were made
sudo service postgresql restart

## Steps to Switch Java Versions:
1. Check available Java versions:
```bash
sudo update-alternatives --config java
```
2. Switch to Java 17: Select the number corresponding to Java 17 from the list to set it as the default.

3. Verify Java version: Check that the correct version is being used by running:
```bash
java -version
```
4. Verify Maven version: Finally, confirm that Maven is now using Java 17:
```bash
mvn -v
```