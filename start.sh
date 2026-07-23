#!/bin/bash
set -euo pipefail

if [ ! -f .env ]; then
  echo "Arquivo .env nao encontrado. Copie .env.example para .env e preencha os valores."
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

./mvnw spring-boot:run
