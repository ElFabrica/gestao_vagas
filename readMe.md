# Gestão de Vagas - Documentação do Projeto

Este documento fornece uma visão geral da aplicação **Gestão de Vagas**, suas tecnologias, estrutura e os comandos necessários para configurar e executar o ambiente de desenvolvimento.

## 1. Introdução

A aplicação **Gestão de Vagas** é um sistema desenvolvido em Java com Spring Boot, projetado para gerenciar vagas de emprego. Ele oferece funcionalidades para empresas e candidatos, utilizando um banco de dados PostgreSQL e segurança baseada em JWT.

## 2. Tecnologias Utilizadas

O projeto `gestao_vagas` é construído com as seguintes tecnologias principais, conforme identificado no arquivo `pom.xml`:

*   **Java 17**: Linguagem de programação.
*   **Spring Boot**: Framework para construção de aplicações Java robustas e escaláveis.
*   **Spring Boot Starter WebMVC**: Para construção de APIs RESTful.
*   **Spring Boot Starter Data JPA**: Para persistência de dados com JPA e Hibernate.
*   **PostgreSQL**: Banco de dados relacional.
*   **Lombok**: Biblioteca para reduzir código boilerplate.
*   **Spring Security**: Para autenticação e autorização.
*   **Java JWT (auth0)**: Para implementação de JSON Web Tokens.
*   **SpringDoc OpenAPI UI**: Para geração automática de documentação de API (Swagger/OpenAPI).
*   **Maven**: Ferramenta de automação de build e gerenciamento de dependências.
*   **Docker Compose**: Para orquestração de contêineres (PostgreSQL e SonarQube).
*   **SonarQube**: Ferramenta de análise de qualidade de código.

## 3. Estrutura do Projeto

A estrutura principal do código-fonte da aplicação está organizada da seguinte forma:

```
src/
├── main/
│   ├── java/
│   │   └── com/example/gestao_vagas/
│   │       ├── config/        # Classes de configuração da aplicação
│   │       ├── exceptions/    # Classes para tratamento de exceções personalizadas
│   │       ├── modules/       # Módulos da aplicação (e.g., Company, Candidate, Job)
│   │       ├── providers/     # Provedores de serviços (e.g., JWTProvider)
│   │       ├── security/      # Configurações de segurança (Spring Security)
│   │       └── GestaoDeVagas.java # Classe principal da aplicação Spring Boot
│   └── resources/
│       └── application.properties # Arquivo de configuração da aplicação
└── test/
    └── java/
        └── com/example/gestao_vagas/
            └── ... # Testes unitários e de integração
```

## 4. Configuração e Execução do Ambiente

Para configurar e executar a aplicação, siga os passos abaixo:

### 4.1. Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

*   **Java Development Kit (JDK) 17** ou superior.
*   **Maven**.
*   **Docker** e **Docker Compose**.

### 4.2. Banco de Dados (PostgreSQL) com Docker Compose

O banco de dados PostgreSQL pode ser iniciado facilmente usando o Docker Compose. O arquivo `docker-compose.yml` define um serviço `postgres`.

Para iniciar o banco de dados, navegue até a raiz do projeto e execute:

```bash
docker-compose up -d
```

Isso iniciará um contêiner PostgreSQL com as seguintes configurações:

*   **Nome do contêiner**: `vagas-db`
*   **Porta**: `5432` (mapeada para a porta `5432` do host)
*   **Usuário**: `postgres`
*   **Senha**: `postgres`
*   **Banco de dados**: `gestao_vagas`

### 4.3. SonarQube (Análise de Qualidade de Código)

O projeto inclui comandos para integração com o SonarQube para análise de qualidade de código. Para iniciar o SonarQube e executar a análise, utilize os seguintes comandos:

1.  **Iniciar o SonarQube (via Docker)**:

    ```bash
    docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:9.9.0-community
    ```

2.  **Executar a análise com Maven**: (Certifique-se de que o SonarQube esteja rodando na porta `9000`)

    ```bash
    mvn clean verify sonar:sonar \
      -Dsonar.projectKey=gestao_vagas \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.login=sqp_354026640f6580bdae9f5a1319be484daf13855b
    ```

    **Nota**: O token `sonar.login` (`sqp_354026640f6580bdae9f5a1319be484daf13855b`) é um exemplo e deve ser substituído por um token de autenticação válido do seu servidor SonarQube, caso o projeto seja configurado em um ambiente real.

### 4.4. Execução da Aplicação Spring Boot

Após configurar o banco de dados, você pode iniciar a aplicação Spring Boot. Navegue até a raiz do projeto e execute:

```bash
mvn spring-boot:run
```

Alternativamente, você pode construir o projeto e executar o JAR gerado:

```bash
mvn clean install
java -jar target/gestao_vagas-0.0.1-SNAPSHOT.jar
```

## 5. Comandos Essenciais

| Comando | Descrição |
| :------ | :-------- |
| `docker-compose up -d` | Inicia o contêiner PostgreSQL em segundo plano. |
| `docker-compose down` | Para e remove os contêineres definidos no `docker-compose.yml`. |
| `docker run -d --name sonarqube ...` | Inicia o servidor SonarQube em um contêiner Docker. |
| `mvn clean verify sonar:sonar ...` | Executa a análise de qualidade de código com SonarQube. |
| `mvn spring-boot:run` | Inicia a aplicação Spring Boot no modo de desenvolvimento. |
| `mvn clean install` | Compila o projeto, executa testes e empacota a aplicação em um JAR. |
| `java -jar target/gestao_vagas-0.0.1-SNAPSHOT.jar` | Executa a aplicação Spring Boot a partir do arquivo JAR gerado. |

## 6. Conclusão

Esta documentação visa facilitar a compreensão e o setup do projeto `gestao_vagas`. Para mais detalhes sobre a implementação, consulte o código-fonte nos respectivos módulos.

---
