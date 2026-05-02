# CineAlert Backend — WalkThrough

## O que é a aplicação

**CineAlert** é um app de lembretes de filmes e séries. O backend é uma REST API que permite ao usuário cadastrar lembretes para assistir conteúdos, receber notificações via Firebase Cloud Messaging (FCM) e buscar informações de filmes/séries via IMDB API (RapidAPI).

---

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.3 |
| Banco de dados | MongoDB 7 |
| Autenticação | JWT (jjwt 0.12.5) + Spring Security |
| Notificações | Firebase Admin SDK 9.2.0 |
| Email | Spring Mail (Gmail SMTP) |
| API externa | IMDB via RapidAPI (`imdb8.p.rapidapi.com`) |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven 3.9 |

---

## Arquitetura

Clean Architecture com 3 camadas principais:

```
entrypoint/        → Controllers REST + DTOs de request/response
core/              → Modelos de domínio, interfaces de repositório, use cases, serviços
dataprovider/      → Entidades MongoDB, implementações de repositório, cliente IMDB
configuration/     → Spring Security, JWT, Firebase, CORS, Swagger, exception handler
```

---

## Collections MongoDB

| Collection | Descrição |
|-----------|-----------|
| `users` | Usuários cadastrados (email único, bcrypt senha, FCM token) |
| `reminders` | Lembretes de conteúdos (status: PENDING/SENT/CANCELLED, recorrência: ONCE/DAILY/WEEKLY, `zone_id` para timezone do usuário) |
| `contents` | Cache de filmes/séries buscados no IMDB |

### Campo `zone_id` em `reminders`

Cada documento de lembrete armazena o timezone do dispositivo no momento da criação:

```json
{
  "scheduled_at": "2025-05-01T20:00:00",
  "zone_id":      "America/Sao_Paulo",
  "status":       "PENDING"
}
```

- `scheduled_at` — sempre UTC (indexado para o scheduler)
- `zone_id` — IANA zone ID ou offset (ex: `"America/Sao_Paulo"`, `"-03:00"`) extraído do `ZonedDateTime` enviado pelo mobile
- Lembretes recorrentes herdam o `zone_id` do lembrete original

---

## Endpoints principais

| Grupo | Base path |
|-------|-----------|
| Autenticação | `/api/auth/**` (register, login, refresh, forgot-password) |
| Usuário | `/api/users/**` |
| Conteúdos (IMDB) | `/api/contents/**` |
| Lembretes | `/api/reminders/**` |
| Notificações FCM | `/api/notifications/**` |
| Health / Actuator | `/actuator/health`, `/actuator/info` |
| Swagger UI | `/swagger-ui.html` |
| OpenAPI docs | `/api-docs` |

Endpoints públicos (sem auth): `/api/auth/**`, `/swagger-ui/**`, `/api-docs/**`, `/actuator/health`

---

## Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `MONGO_URI` | URI de conexão MongoDB com credenciais |
| `JWT_SECRET` | Chave secreta JWT (mínimo 64 caracteres) |
| `IMDB_API_KEY` | Chave da API RapidAPI (IMDB8) |
| `FIREBASE_ENABLED` | `true` ou `false` |
| `FIREBASE_CREDENTIALS_PATH` | Caminho para o JSON de credenciais Firebase |
| `MAIL_USERNAME` | Email Gmail para envio |
| `MAIL_PASSWORD` | App Password do Gmail |
| `CORS_ORIGINS` | Origens permitidas (ex: `https://seusite.com`) |

---

## Profiles Spring

| Profile | Uso |
|---------|-----|
| (default) | Desenvolvimento local — logs DEBUG, MongoDB em localhost |
| `prod` | Produção AWS — logs INFO/WARN, forward-headers-strategy para HTTPS |

---

## Infraestrutura AWS (produção)

| Componente | Detalhe |
|-----------|---------|
| EC2 | Ubuntu 22.04 LTS, t3.small, us-east-1 |
| IP | Elastic IP `54.166.55.238` associado à instância |
| Domínio | `cinealert.link` registrado no Route 53 |
| DNS | `api.cinealert.link` → `54.166.55.238` |
| Reverse proxy | Traefik v2.11 com TLS automático (Let's Encrypt) |
| Certificado TLS | Let's Encrypt via tlsChallenge (porta 443) |
| Containers | Docker + Docker Compose (`docker-compose.prod.yml`) |

### Serviços Docker em produção

```
cinealert-traefik        → Traefik v2.11 (portas 80, 443, 8080)
cinealert-backend        → Spring Boot (porta 8080 interna)
cinealert-mongodb        → MongoDB 7 (porta 27017 interna)
cinealert-mongo-express  → Mongo Express (porta 8081 interna)
```

### URLs de produção

| Serviço | URL |
|---------|-----|
| API base | `https://api.cinealert.link` |
| Swagger UI | `https://api.cinealert.link/swagger-ui.html` |

---

## Arquivos relevantes no projeto

| Arquivo | Descrição |
|---------|-----------|
| `docker-compose.yml` | Compose para desenvolvimento local |
| `docker-compose.prod.yml` | Compose de produção (Traefik + MongoDB + Backend + Mongo Express) |
| `traefik/traefik.yml` | Configuração estática do Traefik (tlsChallenge, entrypoints) |
| `traefik/acme.json` | Certificados Let's Encrypt (chmod 600, nunca commitar) |
| `src/main/resources/application.yml` | Configuração base (todos os profiles) |
| `src/main/resources/application-prod.yml` | Overrides de produção |
| `.env.prod` | Segredos de produção (somente no servidor, nunca no repositório) |
| `Dockerfile` | Multi-stage build: eclipse-temurin:21-jdk-alpine → jre-alpine |

---

## Últimas alterações realizadas

### Suporte a timezone nas datas (ReminderRequestDto / ReminderResponseDto)

**Problema resolvido:** o mobile estava enviando `scheduledAt` em UTC (`"...Z"`), perdendo a informação do timezone do usuário. As respostas devolviam `LocalDateTime` sem indicação de zona.

**Contrato atual da API de lembretes:**

- **Request** (`POST /api/reminders`, `PUT /api/reminders/{id}`): `scheduledAt` deve ser enviado como `ZonedDateTime` ISO 8601 com offset (ex: `"2025-05-01T17:00:00-03:00"`). O backend converte para UTC e persiste o `zone_id` extraído do valor.
- **Response**: `scheduledAt` e `createdAt` são devolvidos como `ZonedDateTime` no timezone armazenado no `zone_id` do lembrete (ex: `"2025-05-01T17:00:00-03:00"`). Fallback para UTC se `zone_id` for nulo ou inválido.

**Arquivos alterados:**

| Arquivo | O que mudou |
|---------|-------------|
| `ReminderRequestDto` | `scheduledAt`: `LocalDateTime` → `ZonedDateTime`; `toModel()` extrai `zone_id` via `scheduledAt.getZone().getId()` |
| `ReminderResponseDto` | `scheduledAt` e `createdAt`: `LocalDateTime` → `ZonedDateTime`; conversão UTC → zone do lembrete em `fromModel()` |
| `ReminderEntity` | Novo campo `@Field("zone_id") String zoneId` |
| `Reminder` / `ReminderRequest` / `ReminderResponse` | Novo campo `zoneId` propagado por todas as camadas |
| `NotificationSchedulerUseCaseImpl` | `LocalDateTime.now()` → `LocalDateTime.now(ZoneOffset.UTC)` (comparação explicitamente UTC); `zoneId` propagado para lembretes recorrentes |

---

### `application.yml`
- `FIREBASE_CREDENTIALS_PATH` agora tem default vazio (`${FIREBASE_CREDENTIALS_PATH:}`) para evitar falha de startup quando a variável não estiver definida

### `application-prod.yml` (criado)
- Profile `prod` com override de logging (DEBUG → INFO/WARN)
- `management.health.mail.enabled: false`
- `server.forward-headers-strategy: framework` — necessário para o Swagger gerar URLs `https://` quando a aplicação está atrás do Traefik (SSL termination)

### `docker-compose.prod.yml` (criado)
- Stack completa de produção com Traefik v2.11
- Variável `DOMAIN` centralizada nos labels do Traefik
- MongoDB sem portas expostas publicamente (apenas rede interna `cinealert-network`)
- Backend com `SPRING_PROFILES_ACTIVE=prod`
- TLS automático via Let's Encrypt (`certresolver=letsencrypt`)

### `traefik/traefik.yml` (criado)
- Entrypoints `web` (80→443 redirect) e `websecure` (443)
- Resolver Let's Encrypt com **tlsChallenge** (porta 443)
- Docker provider com `exposedByDefault: false`

---

## Comandos úteis no servidor

```bash
# Subir a stack
docker compose -f ~/cinealert/cine-alert-backend/docker-compose.prod.yml \
  --env-file ~/cinealert/.env.prod up -d

# Ver logs do backend
docker compose -f ~/cinealert/cine-alert-backend/docker-compose.prod.yml \
  --env-file ~/cinealert/.env.prod logs -f backend

# Ver logs do Traefik
docker compose -f ~/cinealert/cine-alert-backend/docker-compose.prod.yml \
  --env-file ~/cinealert/.env.prod logs -f traefik

# Rebuild após código novo
git pull && docker compose -f ~/cinealert/cine-alert-backend/docker-compose.prod.yml \
  --env-file ~/cinealert/.env.prod up -d --build --force-recreate backend

# Status dos containers
docker compose -f ~/cinealert/cine-alert-backend/docker-compose.prod.yml ps
```
