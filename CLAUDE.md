# OrbitAPI

## O que é

API REST do OrbitWeb, em Spring Boot. O Orbit é uma aplicação de produtividade pessoal — tarefas,
agenda, calendário, cronômetro de estudos, histórico e revisão semanal — e esta API vai concentrar
as regras de negócio, a persistência em PostgreSQL e os endpoints que o front consome.

O projeto saiu do template `ByGustavoo/SpringBootTemplate` e foi construído endpoint a endpoint;
os 30 endpoints do índice do contrato estão implementados. O que a API faz está especificado no
OrbitWeb (`D:\Projetos\OrbitWeb`, repositório `ByGustavoo/OrbitWeb`), que hoje roda contra um
simulador:

| Documento no OrbitWeb | Conteúdo |
|---|---|
| `docs/api-contrato.md` | Os 30 endpoints: rotas, corpos, respostas, códigos HTTP e erros. **Referência final dos formatos JSON** |
| `docs/backend.md` | Entidades, enums, DTOs, validações e fluxos do ponto de vista de quem implementa |
| `docs/regras-negocio.md` | Regras funcionais com exemplos (prazo, recorrência, métricas) |
| `src/dados/simulacao/manipuladores/` | O simulador: a implementação de referência quando o contrato deixar dúvida |

Os quatro documentos têm cópia idêntica em `docs/` deste repositório. Ao mudar o contrato, edite a
cópia daqui e copie para o OrbitWeb, para as duas continuarem iguais.

Mudou comportamento aqui, muda o contrato lá no mesmo trabalho. Onde o contrato diz
**A DEFINIR NO BACKEND**, a decisão é tomada aqui e registrada de volta nele.

## Pendências da implementação

Itens de endpoints já entregues que dependem de algo ainda não construído. Resolva no endpoint
indicado e remova daqui:

Nenhuma no momento.

## Tipo

Backend / API — release: branch + Pull Request (`origin`: `ByGustavoo/OrbitAPI`).

## Stack

- Java 25 (toolchain em `build.gradle.kts`), Gradle 9.6.1 com Kotlin DSL
- Spring Boot 4.1.1: Web MVC, Data JPA, Validation, DevTools
- PostgreSQL + Flyway (`spring-boot-starter-flyway`, `flyway-database-postgresql`)
- Redis como cache (`spring-boot-starter-cache`, `spring-boot-starter-data-redis`), no mesmo modelo do
  PrismaAPI
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
  config/                    DataBaseConfig (DataSource dos perfis dev e prod), CorsConfig,
                             ValidationConfig (EL de métodos nas mensagens de validação),
                             ClockConfig (Clock em UTC, injetado nos services),
                             RedisConfig (conexão, chave, serialização e tolerância a falha do cache)
  service/fuso/              FusoHorarioService: o ZoneId do X-Fuso-Horario da requisição atual
  validation/                restrições de classe (TarefaConsistente, SessaoConsistente) que precisam de
                             mais de um campo; o validador de sessão recebe Clock e FusoHorarioService
                             por injeção, porque compara os horários com "agora"
src/main/resources/
  application.yaml           config base + perfis dev, prod e test
  log4j2.xml                 console sempre; em prod, arquivo rotativo em /app/logs/OrbitAPI.log (30 dias)
.run/                        run configurations do IntelliJ (BootRun DEV/PROD, build sem testes, testes)
docker-compose-postgres.yml  orbit-postgres (PostgreSQL 18, banco orbit) na 5433 e orbit-redis na 6380
docker-compose-orbitapi.yml  a imagem publicada (OrbitAPI, perfil prod, 9028) e o orbit-redis; banco e CORS
                             vêm do .env (modelo em .env.example)
Dockerfile                   build com Gradle e runtime em eclipse-temurin:25-jre
.github/workflows/           workflow.yml (build + testes em todo PR para a main) e release.yml
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
Swagger UI responde em `http://localhost:9018/OrbitAPI/swagger-ui.html`. `tasks.test` é
`finalizedBy(jacocoTestReport)`, e o relatório HTML sai em `build/reports/jacoco`.

O aviso `Error opening zip file ... byte-buddy-agent` no `./gradlew test` vem do acento em
`Usuário` no caminho do cache do Gradle e não afeta os testes: confira o resultado em
`build/test-results/test`.

## Docker e release

Mesmo modelo do PrismaAPI e do OrbitWeb:

- **CI** (`workflow.yml`): em todo PR para a `main`, sobe um PostgreSQL 18 na 5433 e roda
  `./gradlew build jacocoTestReport`. Os secrets `POSTGRES_DATABASE`, `POSTGRES_USER` e
  `POSTGRES_PASSWORD` criam o banco e chegam aos testes como `DATABASE_TEST_NAME`, `DATABASE_USER` e
  `DATABASE_PASSWORD`
- **Release** (`release.yml`): no merge de um PR na `main` (ou manual, escolhendo o incremento),
  calcula a versão a partir da última tag `vX.Y.Z` (sem tag, usa o `version` do `build.gradle.kts`),
  com rótulo `release:major` / `release:minor` no PR, `patch` sem rótulo. Publica a imagem
  `linux/amd64` e `linux/arm64` no Docker Hub com a versão e `latest`, e cria a tag e a release no
  GitHub. Secrets: `DOCKER_IMAGE` (`gurudohimalaia/orbitapi`), `DOCKER_USERNAME` e `DOCKER_PASSWORD`
- A versão chega ao jar por `-Pversao` (`version = providers.gradleProperty("versao")...`); não volte
  o `version` para um literal simples
- **CORS em produção.** O perfil `prod` só libera `http://localhost:5173`, e o OrbitWeb em container
  roda na 9031 (a 5173 é do PrismaWeb). O `docker-compose-orbitapi.yml` sobrescreve as origens por
  `ORBITAPI_CORS_ORIGENS_PERMITIDAS`, com padrão `http://localhost:9031`
- **Banco a partir do container.** `DATABASE_IP=localhost` aponta para o próprio container; use o IP
  da máquina ou `host.docker.internal`

## Perfis e ambiente

- Toda rota vive sob o context path `/OrbitAPI`
- `dev` — porta 9018, `format_sql` ligado, `org.hibernate.SQL` em DEBUG e binder em TRACE
- `prod` — porta 9028, log também em arquivo rotativo
- `test` — o DataSource vem do `TestDataBaseConfig` (em `src/test`), que aponta para o banco
  **`orbit_teste`**, no mesmo `orbit-postgres`, e não para o `orbit` do dev. O nome vem de
  `DATABASE_TEST_NAME` (e não de `DATABASE_NAME`) para que uma variável de dev nunca leve os testes
  ao banco de dev. O banco precisa existir:
  `docker exec orbit-postgres psql -U postgres -c "CREATE DATABASE orbit_teste"`
- **Massa de testes** em `src/test/resources/db/test/R__PopularBanco.sql`, lida só no perfil `test`
  (`spring.flyway.locations`). É uma migration **repetível**, e não `V<próxima>__`, como no modelo da
  skill: com migrations criadas uma por endpoint, um número fixo ficaria para trás da próxima
  versionada. O Flyway roda a repetível depois das versionadas e a reaplica quando o conteúdo
  muda, então ela começa com `TRUNCATE ... RESTART IDENTITY CASCADE` das tabelas que preenche.
  As categorias não entram nela: vêm da `V1.0`, que as semeia também em produção
- No `test`, o `RedisConfig` não vale (`@Profile({"dev", "prod"})`) e `spring.cache.type: none` desliga o
  cache, então os testes não dependem de um Redis rodando
- Variáveis obrigatórias em `dev` e `prod`, lidas pelo `DataBaseConfig`: `DATABASE_IP`,
  `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`, e pelo `RedisConfig`:
  `REDIS_IP` e `REDIS_PORT`. Sem elas a aplicação não sobe. `REDIS_PASSWORD` é opcional, porque o
  `orbit-redis` sobe sem senha. As run configurations em `.run/` já as definem com os valores do
  `docker-compose-postgres.yml` (`localhost:5433/orbit` e `localhost:6380`)
- Flyway usa o schema `orbitapi` (`schemas` e `default-schema`), com `baseline-on-migrate`

## Convenções

- Pacote raiz `br.com.orbitapi`; identificadores de negócio em português
- Use a skill `java-clean-architecture` para qualquer código Java (layout, exceções, estilo e o
  modelo de testes) e `release-project` para commitar ou abrir PR
- `spring.jpa.open-in-view: false` — carregue o que a resposta precisa dentro da transação
- Rotas vêm do `api-contrato.md` do OrbitWeb, em português e kebab-case sem acento, sob o prefixo
  `/v1` (`/v1/tarefas`, `/v1/tarefas/resumo-calendario`, `/v1/revisao-semanal`). Métodos de
  controller levam o nome da ação, não do verbo HTTP
- Migrations em `V1.<n>__<Acao>.sql` (`V1.0__CriarCategorias.sql`), uma por recurso, na ordem em que
  os endpoints são implementados
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
- **URL base do front.** O OrbitWeb lê `VITE_URL_API`, cujo padrão no código é
  `http://localhost:8080/api`. Com esta API em dev, a URL é `http://localhost:9018/OrbitAPI/v1`
  (já no `.env.example` do OrbitWeb e no `api-contrato.md`, seção 1.1)
- **CORS.** O front envia o cabeçalho próprio `X-Fuso-Horario`, que dispara preflight. O
  `CorsConfig` libera `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` e os cabeçalhos
  `Content-Type`, `Accept` e `X-Fuso-Horario` para as origens de `orbitapi.cors.origens-permitidas`:
  qualquer porta de `localhost` em `dev` (a 5173 costuma estar ocupada pelo PrismaWeb) e
  `http://localhost:5173` nos demais perfis
- **"Hoje" depende do fuso do cliente.** `X-Fuso-Horario` (IANA) define o dia de cada instante e
  quando uma tarefa fica atrasada (`api-contrato.md`, seção 1.4). Nunca use `LocalDate.now()` num
  service: calcule `Instant.now(clock)` e converta com `fusoHorarioService.obter()`. Sem o
  cabeçalho, ou com valor inválido, vale `America/Sao_Paulo`
- **`CURRENT_DATE` do banco é UTC.** O `orbit-postgres` roda em UTC; à noite no Brasil, o
  `CURRENT_DATE` da massa de testes já é o dia seguinte ao `LocalDate.now()` da JVM. Teste que
  compara com a data de uma tarefa semeada usa a própria data dela, não `LocalDate.now()`
- **O prazo existe em dois lugares.** `PrazoService` (Java, para uma tarefa) e
  `TarefaPrazoSql.TAREFAS_COM_PRAZO` (CTE nativa usada pela lista, pelo calendário e pelo
  Histórico) implementam a mesma regra. Mudou uma, mude a outra; o teste manual do `GET /v1/tarefas` compara as duas em
  todas as tarefas, nos fusos `America/Sao_Paulo` e `Asia/Tokyo`
- **Só fusos IANA.** O SQL calcula o limite com `AT TIME ZONE :fuso`, e o PostgreSQL lê um
  deslocamento fixo (`-03:00`) com o sinal invertido. Por isso o `FusoHorarioService` só aceita IDs
  de `ZoneId.getAvailableZoneIds()`
- **Leituras que mostram tarefas futuras estendem as séries antes.** Endpoints que listam tarefas
  por data (por id, lista, calendário, Revisão semanal e os do Dashboard que olham para a frente) chamam
  `serieRecorrenciaService.estender(agora)` no início, dentro de uma transação de escrita, e não
  `readOnly`. Sem isso, uma série sem término some do futuro depois de 12 meses. O Histórico não
  precisa: a extensão só cria ocorrências depois de `gerada_ate`, que não mudam nenhum prazo do
  passado
- **Filtro opcional não usa `:param IS NULL`.** Em JPQL ou SQL, `(:data IS NULL OR ...)` com o
  parâmetro nulo chega ao PostgreSQL sem tipo e falha ("could not determine data type of parameter").
  Monte só os filtros informados: `Specification` (como `SessaoSpecification`) ou SQL dinâmico com
  parâmetros (como `TarefaConsultaRepositoryImpl`)
- **Validação da tarefa em duas etapas.** O `TarefaEnvioDTO` normaliza no construtor compacto e
  valida as regras entre campos em `@TarefaConsistente`, que devolve cada erro no nome do campo
  que o front conhece (`horarioFim`, `frequencia`, `diasSemana`, `dataFim`, e não
  `recorrencia.frequencia`). Categoria e atividade são checadas depois, no `TarefaService`, e viram
  `400` com `errors` pelo handler
- **Portas deslocadas de propósito.** O PrismaAPI roda na mesma máquina e ocupa 5432, 6379, 9017 e
  9027, com containers chamados `postgres` e `Redis`. O Orbit usa `orbit-postgres` na 5433,
  `orbit-redis` na 6380 e a API em 9018 (dev) e 9028 (prod), para os dois subirem juntos. Não volte
  para as portas ou nomes do template
- **Normalização antes da validação.** O contrato manda limpar a entrada antes de validar (nome sem
  espaços nas pontas e sem repetidos). Isso é feito no construtor compacto do record de envio
  (`CategoriaEnvioDTO`), para o Bean Validation já receber o valor limpo. O `ValidationConfig` liga
  o nível `bean-methods` da EL do Hibernate Validator, que permite mensagens como
  `Agora são ${validatedValue.length()}!`
- **Acentos no `curl` do Git Bash.** Um corpo com acento passado direto em `-d` chega fora de UTF-8 e
  a API responde 400 de JSON ilegível. Para testar à mão, grave o JSON num arquivo em UTF-8 e envie
  com `--data-binary @arquivo.json`
- **Cache só nas leituras que dependem do dia, não do instante.** Passam pelo Redis (`@Cacheable`, TTL de
  15 minutos): `categorias`, `atividades`, `sessoes`, os três de `estudos` e a sequência do Dashboard
  (cache `dashboard`). Ficam de fora, de propósito, as leituras que calculam o prazo com `agora`: as
  de tarefas (lista, por id, calendário), o resumo do Dashboard, o Histórico e a Revisão semanal. Nelas
  uma tarefa vira `ATRASADA` no minuto em que passa do `horarioFim`, e um cache serviria "no prazo" até
  o TTL vencer. Uma leitura nova só entra no cache se o resultado for função dos dados, do fuso e do dia
- **A chave leva o dia e o fuso do cliente.** O `keyGenerator` do `RedisConfig` monta
  `<hoje no fuso>:<fuso>:<geração>:<Classe>.<método>[parâmetros]`, porque o fuso chega pelo cabeçalho
  e não pelos parâmetros, e muda tanto o dia de cada sessão quanto o "hoje" da sequência. "Hoje" vem
  do `Clock` injetado, não de `LocalDate.now()`
- **Cada escrita limpa os caches que leem a tabela alterada** (`@CacheEvict(allEntries = true)`):
  categoria → `categorias`; atividade → `atividades`, `estudos`, `sessoes` (a sessão mostra nome e cor
  da atividade); sessão → `dashboard`, `estudos`, `sessoes`; tarefa → `categorias` (contagem),
  `dashboard` (conclusões da sequência), `sessoes` (título da tarefa). A nota da semana não afeta
  nenhum. Um `GET` novo em cache, ou uma leitura nova de outra tabela num `GET` já em cache, exige
  revisar essas listas
- **A extensão das séries também escreve tarefas.** `SerieRecorrenciaService.estender` roda dentro de
  leituras e cria ocorrências, então limpa os caches de tarefa, mas só quando estendeu alguma série
  (`condition = "#result"`, e por isso devolve `boolean`). Limpar sempre esvaziaria o cache a cada
  `GET` de tarefas
- **O cache nunca derruba a API.** Com o Redis fora, o `errorHandler` do `RedisConfig` registra a falha
  e a leitura vai ao banco; o Lettuce recusa comandos enquanto está desconectado e desiste em 2 s. Uma
  limpeza que falha incrementa a geração da chave, iniciada no instante em que a aplicação sobe, e a
  chave velha nunca mais é lida. Por isso as gravações são imediatas (`immediateWrites`): no modo
  assíncrono, a falha da limpeza não chegaria ao handler
- **O valor vai para o Redis em JSON, sem `Serializable`.** O `TypeResolverBuilder` do `RedisConfig`
  grava o tipo em tudo que não é primitivo, records e listas de `.toList()` inclusive, e registra
  qualquer `List` como `ArrayList`. Campo de DTO em cache precisa voltar do JSON como saiu
- `OrbitAPIApplication.main` é `static void main` sem `public` — sintaxe do Java 25, que o `bootRun` aceita