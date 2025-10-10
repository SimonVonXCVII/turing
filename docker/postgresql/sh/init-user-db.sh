#!/usr/bin/env bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER keycloak WITH PASSWORD 'password';
	CREATE DATABASE keycloak OWNER keycloak;
	GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
	CREATE USER turing WITH PASSWORD 'password';
	CREATE DATABASE turing OWNER turing;
	GRANT ALL PRIVILEGES ON DATABASE turing TO turing;
EOSQL