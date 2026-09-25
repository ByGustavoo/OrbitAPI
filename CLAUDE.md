# OrbitAPI

## O que é

API REST do OrbitWeb, em Spring Boot. O Orbit é uma aplicação de produtividade pessoal — tarefas,
agenda, calendário, cronômetro de estudos, histórico e revisão semanal — e esta API vai concentrar
as regras de negócio, a persistência em PostgreSQL e os endpoints que o front consome.

O projeto acabou de sair do template `ByGustavoo/SpringBootTemplate`: ainda não há entidades,
migrations, endpoints nem testes. O que a API precisa fazer já está especificado no OrbitWeb
(`D:\Projetos\OrbitWeb`, repositório `ByGustavoo/OrbitWeb`), que hoje roda contra um simulador:

| Documento no OrbitWeb | Conteúdo |
|---|---|
| `docs/api-contrato.md` | Os 27 endpoints: rotas, corpos, respostas, códigos HTTP e erros. **Referência final dos formatos JSON** |
| `docs/backend.md` | Entidades, enums, DTOs, validações e fluxos do ponto de vista de quem implementa |
| `docs/regras-negocio.md` | Regras funcionais com exemplos (prazo, recorrência, métricas) |
| `src/dados/simulacao/manipuladores/` | O simulador: a implementação de referência quando o contrato deixar dúvida |

Mudou comportamento aqui, muda o contrato lá no mesmo trabalho. Onde o contrato diz
**A DEFINIR NO BACKEND**, a decisão é tomada aqui e registrada de volta nele.

## Tipo

Backend / API — release: branch + Pull Request (`origin`: `ByGustavoo/OrbitAPI`).

## Stack

- Java 25 (toolchain em `build.gradle.kts`), Gradle 9.6.1 com Kotlin DSL
- Spring Boot 4.1.1: Web MVC, Data JPA, Validation, DevTools
- PostgreSQL + Flyway (`spring-boot-starter-flyway`, `flyway-database-postgresql`)
- MapStruct 1.6.3 + Lombok (com `lombok-mapstruct-binding`)
- Log4j2 — Logback e `spring-boot-starter-logging` são **excluídos** em
  `configurations.configureEach`; não reintroduza dependências que os tragam de volta
- Springdoc OpenAPI 3.1.0 (Swagger UI)
- JUnit 5 + `spring-boot-starter-test` e os starters de teste por módulo do Spring Boot 4
  (`flyway-test`, `webmvc-test`, `data-jpa-test`, `validation-test`); cobertura via JaCoCo

## Estrutura

```
src/main/java/br/com/orbitapi/
  OrbitAPIApplication.java   classe de inicialização
  config/                    DataBaseConfig (DataSource dos perfis dev e prod)
src/main/resources/
  application.yaml           config base + perfis dev, prod e test
  log4j2.xml                 console sempre; em prod, arquivo rotativo em /app/logs/OrbitAPI.log (30 dias)
.run/                        run configurations do IntelliJ (BootRun DEV/PROD, build sem testes, testes)
docker-compose-postgres.yml  orbit-postgres (PostgreSQL 18, banco orbit) na 5433 e orbit-redis na 6380
```

Os pacotes seguem o layout da skill `java-clean-architecture` conforme forem surgindo:
`controller/`, `enums/`, `exceptions/`, `model/dto`, `model/entity`, `model/mapper`,
`repository/`, `service/`; migrations em `src/main/resources/db/migration/`.

## Comandos

| Objetivo | Comando |
|---|---|
| Compilar | `./gradlew classes` |
| Rodar em dev (porta 9018) | `./gradlew bootRun --args="--spring.profiles.active=dev"` |
| Rodar em prod (porta 9028) | `./gradlew bootRun --args="--spring.profiles.active=prod"` |
| Testes + relatório JaCoCo | `./gradlew test` |
| Build sem testes | `./gradlew clean build -x test` |
| Banco local | `docker compose -f docker-compose-postgres.yml up -d` |

`bootRun` exige as variáveis de banco abaixo e o `orbit-postgres` de pé; em dev sobe em ~2 s e o
Swagger UI responde em `http://localhost:9018/OrbitAPI/swagger-ui.html`. Hoje não há testes, então `./gradlew test` termina como `NO-SOURCE`. `tasks.test` é
`finalizedBy(jacocoTestReport)`, e o relatório HTML sai em `build/reports/jacoco`.

## Perfis e ambiente

- Toda rota vive sob o context path `/OrbitAPI`
- `dev` — porta 9018, `format_sql` ligado, `org.hibernate.SQL` em DEBUG e binder em TRACE
- `prod` — porta 9028, log também em arquivo rotativo
- `test` — declarado no `application.yaml`, mas ainda vazio: não existe `TestDataBaseConfig`, então
  um `@SpringBootTest` hoje não tem DataSource
- Variáveis obrigatórias em `dev` e `prod`, lidas pelo `DataBaseConfig`: `DATABASE_IP`,
  `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. Sem elas a aplicação
  não sobe. As run configurations em `.run/` já as definem com os valores do
  `docker-compose-postgres.yml` (`localhost:5433/orbit`)
- Flyway usa o schema `orbitapi` (`schemas` e `default-schema`), com `baseline-on-migrate`

## Convenções

- Pacote raiz `br.com.orbitapi`; identificadores de negócio em português
- Use a skill `java-clean-architecture` para qualquer código Java (layout, exceções, estilo e o
  modelo de testes) e `release-project` para commitar ou abrir PR
- `spring.jpa.open-in-view: false` — carregue o que a resposta precisa dentro da transação
- Rotas vêm do `api-contrato.md` do OrbitWeb, em português e kebab-case sem acento (`/tarefas`,
  `/tarefas/resumo-calendario`, `/revisao-semanal`). Métodos de controller levam o nome da ação,
  não do verbo HTTP
- Mapeamento entidade ↔ DTO com MapStruct
- Nenhum comentário em arquivo do repositório (exceto `.env`/`.env.example`); explicações vão aqui
  ou no README
- Dependências do `build.gradle.kts` agrupadas por ferramenta, grupos separados por linha em branco,
  sem rótulo, e cada grupo da linha mais curta para a mais longa

## Identidade da aplicação

Os nomes abaixo andam juntos — mudar um sem os outros quebra rotas, migrations ou logs:

- `settings.gradle.kts`: `rootProject.name = "OrbitAPI"`; `build.gradle.kts`: `group = "br.com.orbitapi"`
- `application.yaml`: `context-path: /OrbitAPI`, `spring.application.name: orbitapi`, Flyway
  `schema` e `default-schema: orbitapi`
- Entidades JPA, quando existirem: `schema = "orbitapi"`, o mesmo do Flyway
- `log4j2.xml`: em `prod`, `/app/logs/OrbitAPI.log`
- `build.gradle.kts`: o JaCoCo exclui `**/config/**` e `**/OrbitAPIApplication.class`

## Gotchas

- **O contrato do front usa IDs `Long`**, não `UUID` (`backend.md`, seção 2). Tipar entidades e
  repositórios com `Long`, e o `buscar` do `AbstractTest` com `JpaRepository<T, Long>`
- **URL base do front.** O OrbitWeb lê `VITE_URL_API`, cujo padrão é `http://localhost:8080/api`.
  Com esta API em dev, a URL passa a ser `http://localhost:9018/OrbitAPI` (mais o prefixo de versão,
  se for adotado) — ajuste no `.env` do OrbitWeb e no `api-contrato.md`
- **CORS ainda não existe.** O front envia o cabeçalho próprio `X-Fuso-Horario`, que dispara
  preflight: a origem `http://localhost:5173` precisa de `GET`, `POST`, `PUT`, `PATCH`, `DELETE`,
  `OPTIONS` e dos cabeçalhos `Content-Type`, `Accept` e `X-Fuso-Horario`
- **"Hoje" depende do fuso do cliente.** `X-Fuso-Horario` (IANA) define o dia de cada instante e
  quando uma tarefa fica atrasada (`api-contrato.md`, seção 1.4)
- **Portas deslocadas de propósito.** O PrismaAPI roda na mesma máquina e ocupa 5432, 6379, 9017 e
  9027, com containers chamados `postgres` e `Redis`. O Orbit usa `orbit-postgres` na 5433,
  `orbit-redis` na 6380 e a API em 9018 (dev) e 9028 (prod), para os dois subirem juntos. Não volte
  para as portas ou nomes do template
- `OrbitAPIApplication.main` é `static void main` sem `public` — sintaxe do Java 25, que o `bootRun` aceita