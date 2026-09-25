<div align="center"> <br> 
  <img align="center" alt="guru-java" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" />
</div> 

<br> 

<div align="center">
  API REST do <a href="https://github.com/ByGustavoo/OrbitWeb">OrbitWeb</a>, construída em Spring Boot. O Orbit é uma aplicação de produtividade pessoal para organizar tarefas, estudos, agenda e tempo em um só lugar; esta API concentra as regras de negócio, a persistência e os endpoints consumidos pela aplicação web.
</div> 

 <br> 

## 🚀 Ferramentas Utilizadas

* 🐳 Docker

* 🕊️ Flyway

* 📊 JaCoCo

* 📝 Log4j2

* 🔴 Lombok

* ☕️ Java 25

* 🧪 JUnit 5

* 🗺️ MapStruct

* 🐘 PostgreSQL 18

* 🟢 Spring Boot 4.1.1

* 🐘 Gradle 9.6.1 (Kotlin DSL)

* 📄 Springdoc OpenAPI (Swagger UI)

<br> 

## 📌 Status do Projeto

Estrutura inicial pronta a partir do template Spring Boot: identidade da aplicação, perfis,
banco e logging configurados. Entidades, migrations e endpoints ainda serão implementados.

O que a API precisa entregar está especificado no OrbitWeb:

* [`docs/api-contrato.md`](https://github.com/ByGustavoo/OrbitWeb/blob/main/docs/api-contrato.md) — rotas, corpos, respostas e erros

* [`docs/backend.md`](https://github.com/ByGustavoo/OrbitWeb/blob/main/docs/backend.md) — entidades, enums, DTOs e validações

* [`docs/regras-negocio.md`](https://github.com/ByGustavoo/OrbitWeb/blob/main/docs/regras-negocio.md) — regras funcionais com exemplos

<br> 

## ⚙️ Pré-requisitos

* JDK 25 instalada (o projeto não declara resolver de toolchain, então o Gradle não baixa a JDK sozinho)

* PostgreSQL acessível para os perfis `dev` e `prod`

<br> 

## 🔐 Variáveis de Ambiente

Obrigatórias nos perfis `dev` e `prod` (usadas por `DataBaseConfig`):

| Variável | Descrição |
|---|---|
| `DATABASE_IP` | Host do PostgreSQL |
| `DATABASE_PORT` | Porta do PostgreSQL |
| `DATABASE_NAME` | Nome do banco |
| `DATABASE_USER` | Usuário do banco |
| `DATABASE_PASSWORD` | Senha do banco |

<br> 

## ▶️ Como Executar

```bash
# Ambiente de desenvolvimento (porta 9018)
./gradlew bootRun --args="--spring.profiles.active=dev"

# Ambiente de produção (porta 9028)
./gradlew bootRun --args="--spring.profiles.active=prod"
```

No IntelliJ IDEA, as configurações equivalentes estão em `.run/`, já com as variáveis apontando
para o banco do `docker-compose-postgres.yml`.

A aplicação sobe sob o context path `/OrbitAPI`. Em desenvolvimento, a documentação
fica em `http://localhost:9018/OrbitAPI/swagger-ui.html`.

<br> 

## 🧪 Testes e Build

```bash
# Compilar
./gradlew classes

# Testes (gera o relatório JaCoCo em build/reports/jacoco)
./gradlew test

# Build sem testes
./gradlew clean build -x test
```

<br> 

## 🐳 Docker

```bash
# Sobe um PostgreSQL local (banco orbit) na porta 5433 e um Redis na 6380
docker compose -f docker-compose-postgres.yml up -d
```

<br> 

## 📁 Estrutura

```
src/main/java/br/com/orbitapi
├── OrbitAPIApplication.java    # Classe de inicialização
└── config                      # DataBaseConfig

src/main/resources
├── application.yaml            # Configuração por perfil (dev, prod, test)
└── log4j2.xml                  # Configuração de logging
```

<br> 
 
## 🖥️ Desenvolvedor

### 🔵 LinkedIn: [Gustavo Correa](https://www.linkedin.com/in/gustavo-chauar-correa-946168269/)
