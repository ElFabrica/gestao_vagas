# Gestão de Vagas

API REST em Java/Spring Boot para gestão de vagas de emprego, com autenticação JWT separada para **candidatos** e **empresas**, envio assíncrono de e-mails via RabbitMQ, upload de currículo no Cloudflare R2, logs de acesso em MongoDB e observabilidade com Prometheus/Grafana.

## Funcionalidades

- Cadastro e autenticação de candidatos e empresas (JWT)
- CRUD de empresas e candidatos
- Criação de vagas pela empresa
- Listagem de vagas com filtro e candidatura
- Upload de currículo (PDF) no Cloudflare R2
- Perfil do candidato com URL pré-assinada do currículo (quando existir)
- E-mail assíncrono ao candidatar-se (candidato + empresa), com retry e DLQ
- Logs HTTP em MongoDB
- Métricas via Actuator + Prometheus + Grafana
- Documentação OpenAPI (Swagger)

## Stack

| Tecnologia | Uso |
|---|---|
| Java 17 / Spring Boot 4.1 | API |
| Spring Data JPA + PostgreSQL | Domínio (candidatos, empresas, vagas, candidaturas, uploads) |
| MongoDB | Logs de acesso HTTP |
| RabbitMQ | Fila de e-mails + DLQ |
| Cloudflare R2 (API S3) | Armazenamento de currículos (PDF) |
| AWS SDK for Java v2 (`s3`) | Client S3 compatível com R2 |
| JavaMail (SMTP) | Envio de e-mails |
| Spring Security + Auth0 JWT | Autenticação/autorização |
| SpringDoc OpenAPI | Swagger UI |
| Actuator / Micrometer / Prometheus / Grafana | Observabilidade |
| Maven Wrapper (`./mvnw`) | Build e testes |
| Docker Compose | Infra local |

## Estrutura do projeto

```
src/main/java/com/example/gestao_vagas/
├── GestaoDeVagas.java          # Entrypoint
├── config/                     # Swagger, RabbitMQ, Mongo, R2, filtros
├── exceptions/
├── providers/                  # JWT (candidato e empresa)
├── security/                   # SecurityConfig + filtros JWT
└── modules/
    ├── candidate/              # Controllers, use cases, DTOs, producer, consumer
    ├── company/entities/       # Controllers, use cases, Job, Company
    ├── upload/                 # Curriculum upload (R2 + metadados)
    └── logs/                   # Access logs (MongoDB)

src/test/java/.../
├── modules/candidate/useCases/
├── modules/upload/useCases/
├── modules/company/entities/useCases/
└── modules/candidate/company/controllers/   # Teste de integração (MockMvc)
```

## Pré-requisitos

- JDK 17+
- Docker e Docker Compose
- Conta SMTP (ex.: Gmail com App Password) para e-mails
- Conta Cloudflare com bucket R2 (para upload de currículo)

## Setup rápido

### 1. Variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env` e preencha pelo menos `MAIL_USERNAME`, `MAIL_PASSWORD` e as variáveis `R2_*`.

Variáveis principais:

| Variável | Exemplo |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/gestao_vagas` |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | `postgres` / `postgres` |
| `MONGODB_*` | host `localhost`, user/pass `mongo`, DB `gestao_vagas_logs` |
| `RABBITMQ_*` | host `localhost`, porta `5672`, user/pass `guest` |
| `MAIL_HOST` / `MAIL_PORT` | `smtp.gmail.com` / `587` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | credenciais SMTP |
| `R2_ACCOUNT_ID` | Account ID do Cloudflare R2 |
| `R2_ACCESS_KEY` | Access Key ID do token R2 (S3) |
| `R2_SECRET_KEY` | Secret Access Key do token R2 (S3) |
| `R2_BUCKET` | Nome do bucket (ex.: `gestao-vagas`) |
| `R2_PUBLIC_URL` | URL pública do bucket (`https://pub-....r2.dev`) |

No painel Cloudflare R2, ao criar o token S3 use **ID da chave de acesso** e **Chave de acesso secreta**. O **Valor do token** não é usado pela API S3.

Após alterar o `.env`, reinicie a JVM por completo (`./start.sh`). O hot reload do DevTools **não** relê o arquivo `.env`.

### 2. Subir a infraestrutura

```bash
docker compose up -d
```

Serviços:

| Serviço | Container | Portas | Credenciais |
|---|---|---|---|
| PostgreSQL | `vagas-db` | `5432` | `postgres` / `postgres` — DB `gestao_vagas` |
| MongoDB | `vagas-logs-db` | `27017` | `mongo` / `mongo` — DB `gestao_vagas_logs` |
| RabbitMQ | `vagas-rabbitmq` | `5672`, UI `15672` | `guest` / `guest` |
| Prometheus | `prometheus` | `9090` | — |
| Grafana | `grafana` | `3000` | admin padrão do Grafana |

### 3. Rodar a aplicação

Com o script (carrega o `.env` automaticamente):

```bash
./start.sh
```

Ou manualmente:

```bash
set -a && source .env && set +a
./mvnw spring-boot:run
```

A API sobe em **http://localhost:8080**.

## URLs úteis

| Recurso | URL |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |
| Prometheus (app) | http://localhost:8080/actuator/prometheus |
| RabbitMQ Management | http://localhost:15672 |
| Prometheus UI | http://localhost:9090 |
| Grafana | http://localhost:3000 |

## API — endpoints principais

Rotas públicas: cadastro e autenticação. Demais rotas exigem `Authorization: Bearer <token>`.

### Candidato

| Método | Path | Auth | Descrição |
|---|---|---|---|
| `POST` | `/candidate/` | pública | Cadastro |
| `POST` | `/candidate/auth` | pública | Login → JWT (role `CANDIDATE`, ~10 min) |
| `GET` | `/candidate/` | `ROLE_CANDIDATE` | Perfil (inclui URL do currículo, se houver) |
| `PUT` | `/candidate/{id}` | `ROLE_CANDIDATE` | Atualizar |
| `DELETE` | `/candidate/` | `ROLE_CANDIDATE` | Excluir conta |
| `POST` | `/candidate/curriculum` | `ROLE_CANDIDATE` | Upload de currículo (PDF, multipart) |
| `GET` | `/candidate/job?filter=` | `ROLE_CANDIDATE` | Listar vagas |
| `POST` | `/candidate/job/apply` | `ROLE_CANDIDATE` | Candidatar-se (body: UUID da vaga) |

### Empresa

| Método | Path | Auth | Descrição |
|---|---|---|---|
| `POST` | `/company/` | pública | Cadastro |
| `POST` | `/company/auth` | pública | Login → JWT (role `COMPANY`, ~2 h) |
| `GET` | `/company/{id}` | autenticado | Buscar empresa |
| `PUT` | `/company/{id}` | autenticado | Atualizar |
| `DELETE` | `/company/{id}` | autenticado | Excluir |
| `POST` | `/company/job/` | `ROLE_COMPANY` | Criar vaga |

## Upload de currículo (Cloudflare R2)

Fluxo:

1. Candidato autentica e envia `POST /candidate/curriculum` com `multipart/form-data` (campo `file`)
2. A API valida PDF e tamanho máximo de **5 MB**
3. Arquivo é gravado no R2 em `curriculums/{candidateId}/{uuid}.pdf`
4. Metadados ficam na tabela de upload; `candidate.curriculumId` aponta para o CV **ativo**
5. Novo upload substitui o `curriculumId` ativo (histórico permanece na tabela de upload)

### Exemplo de upload

```bash
curl -X POST http://localhost:8080/candidate/curriculum \
  -H "Authorization: Bearer <JWT_CANDIDATO>" \
  -F "file=@/caminho/do/curriculo.pdf"
```

```js
const formData = new FormData();
formData.append("file", file); // nome do campo = "file"

await fetch("http://localhost:8080/candidate/curriculum", {
  method: "POST",
  headers: { Authorization: `Bearer ${token}` },
  body: formData,
});
```

### Perfil e URL do currículo

`GET /candidate/` devolve `candidateUrl`:

| Situação | `candidateUrl` |
|---|---|
| Candidato sem currículo | `null` |
| Currículo ativo e arquivo no R2 | URL pré-assinada (válida por **15 minutos**) |
| `curriculumId` sem registro de upload | `null` |
| Registro existe, mas arquivo foi removido do R2 | `null` |

A verificação no storage usa `HeadObject` (`R2StorageService.exists`). A URL não é persistida — é gerada sob demanda com `presignGet`.

## Segurança

- Senhas com BCrypt
- Dois JWTs distintos (secrets separados para candidato e empresa)
- Filtros `SecurityCandidateFilter` (`/candidate*`) e `SecurityCompanyFilter` (`/company*`)
- Subject do token = ID do usuário; claim `roles` → `ROLE_CANDIDATE` / `ROLE_COMPANY`
- Upload de currículo exige `ROLE_CANDIDATE`; CORS do Spring libera `localhost:3000` e `localhost:5173`

## Fluxo de e-mail (RabbitMQ)

Ao candidatar-se (`ApplyJobCandidateUseCase`):

1. Persiste a candidatura no PostgreSQL
2. Publica `ApplyJobEmailMessageDTO` na exchange `application-email-exchange` (RK `email`)
3. Fila `application-email-queue` → `ApplyJobEmailConsumer`
4. Consumer envia 2 e-mails via SMTP (candidato e empresa)

Em falha: até **3 retries** com backoff; depois a mensagem vai para a DLQ `application-email-queue.dlq` (DLX `application-email-dlx`, RK `email.dlq`).

Mensagens na DLQ **não voltam sozinhas** — republicar manualmente pela UI do RabbitMQ ou via reprocessamento no código.

## Persistência

- **PostgreSQL**: candidatos, empresas, vagas, candidaturas (`apply_jobs`), metadados de upload
- **MongoDB**: collection `access_logs` (filtro HTTP; desligável com `HTTP_ACCESS_LOGS_ENABLED=false`)
- **Cloudflare R2**: arquivos PDF de currículo

## Testes

Testes de use case são unitários (JUnit 5 + Mockito). Não precisam de PostgreSQL, RabbitMQ, SMTP nem R2 real.

```bash
# Todos os testes
./mvnw test

# Apenas use cases
./mvnw -Dtest='**/useCases/**Test' test

# Upload e perfil (currículo)
./mvnw -Dtest=UploadCurriculumUseCaseTest,ProfileCandidateUseCaseTest test

# Um teste específico
./mvnw -Dtest=ApplyJobCandidateUseCaseTest test
./mvnw -Dtest=CreateCompanyUseCaseTest test
```

Cobertura relevante do módulo de currículo:

- `UploadCurriculumUseCaseTest` — upload ok, troca de CV, validações (vazio, >5 MB, não-PDF), candidato inexistente
- `ProfileCandidateUseCaseTest` — perfil com/sem CV, upload ausente no banco, arquivo removido do storage

No IDE: botão direito na pasta `useCases` em `src/test/java` → **Run Tests**.

## Comandos essenciais

| Comando | Descrição |
|---|---|
| `docker compose up -d` | Sobe Postgres, MongoDB, RabbitMQ, Prometheus e Grafana |
| `docker compose down` | Para e remove os containers |
| `cp .env.example .env` | Cria o arquivo de variáveis de ambiente |
| `./start.sh` | Carrega `.env` e sobe a API |
| `./mvnw spring-boot:run` | Sobe a API (env já exportado) |
| `./mvnw test` | Roda todos os testes |
| `./mvnw -Dtest='**/useCases/**Test' test` | Roda testes dos use cases |
| `./mvnw -Dtest=UploadCurriculumUseCaseTest,ProfileCandidateUseCaseTest test` | Testes de currículo/perfil |
| `./mvnw clean package` | Gera o JAR em `target/` |
| `java -jar target/gestao_vagas-0.0.1-SNAPSHOT.jar` | Executa o JAR |

## SonarQube (opcional)

```bash
docker run -d --name sonarqube \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  -p 9000:9000 sonarqube:9.9.0-community
```

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=gestao_vagas \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<SEU_TOKEN>
```

## Deploy

Há um workflow em `.github/workflows/prod.yml` que builda a imagem Docker (`elfabrica/gestao_vagas`) e faz deploy na porta `8080`. O `Dockerfile` é multi-stage e expõe a porta 8080.

Em produção, configure as variáveis `R2_*` no ambiente do container/host.
