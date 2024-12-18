#!/bin/bash
# Source: https://dev.to/nietzscheson/multiples-postgres-databases-in-one-service-with-docker-compose-4fdf

set -e
set -u

function create_user_and_database() {
    local database=$1
    echo "  Creating user and database '$database'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
        CREATE USER "$database" WITH PASSWORD '$database';
        CREATE DATABASE "$database";
        ALTER USER "$database" SET search_path TO "$database";
        GRANT ALL PRIVILEGES ON DATABASE "$database" TO "$database";
EOSQL
    psql -v ON_ERROR_STOP=1 --username "$database" --dbname "$database" <<-EOSQL
        CREATE SCHEMA "$database";
EOSQL
}

if [ -n "${POSTGRES_ADDITIONAL_DATABASES:-}" ]; then
    echo "Multiple database creation requested: $POSTGRES_ADDITIONAL_DATABASES"
    for db in $(echo $POSTGRES_ADDITIONAL_DATABASES | tr ',' ' '); do
        create_user_and_database $db
    done
    echo "Multiple databases created"
fi
