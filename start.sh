#!/bin/bash

export DATABASE_URL="jdbc:postgresql://localhost:5432/gestao_vagas"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="postgres"
export MONGODB_HOST="localhost"
export MONGODB_PORT="27017"
export MONGODB_DATABASE="gestao_vagas_logs"
export MONGODB_USERNAME="mongo"
export MONGODB_PASSWORD="mongo"
export MONGODB_AUTHENTICATION_DATABASE="admin"
export DATABASE_URL=jdbc:postgresql://localhost:5432/gestao_vagas
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
export MAIL_USERNAME=seuemail@gmail.com
export MAIL_PASSWORD="uma senha gerada no https://myaccount.google.com/apppasswords"


./mvnw spring-boot:run
