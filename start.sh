#!/bin/bash

export DATABASE_URL="jdbc:postgresql://localhost:5432/gestao_vagas"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="postgres"

./mvnw spring-boot:run