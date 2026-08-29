# GreenTrails — AGENTS.md

## Project Overview

GreenTrails is a web portal for creating and booking eco-sustainable itineraries.
It is a university project (Università degli Studi di Salerno, a.a. 2025/2026) spanning three courses:
- **Ingegneria del Software: Tecniche Avanzate** — evolutionary maintenance
- **Software Dependability** — perfective maintenance (reliability & security)
- **Sustainable Software Engineering** — perfective maintenance (sustainability)

## Tech Stack

### Backend (`./backend`)
- Java 21, Spring Boot 3.2.1, Maven
- Spring Data JPA, Spring Security, Spring Web MVC, Spring Validation
- MySQL (production) / H2 (test/dev)
- Lombok, Checkstyle (Google style), JaCoCo, Pitest (mutation testing), JMH (microbenchmarks)

### Frontend (`./frontend`)
- Angular 14, Angular Material 13, Bootstrap 5
- ng-bootstrap, ngx-cookie-service, ng2-search-filter

### Infrastructure
- Docker Compose (`docker-compose.yml`, `docker-compose.prod.yml`, `docker-compose.test.yml`)

## Project Structure

```
GreenTrails/
├── backend/
│   ├── src/main/java/it/greentrails/backend/
│   │   ├── BackendApplication.java
│   │   ├── entities/          — JPA entities
│   │   ├── enums/             — Enumerations
│   │   ├── utils/             — Utility classes
│   │   ├── gestioneutenze/    — User management (incl. security)
│   │   ├── gestioneattivita/  — Activities management
│   │   ├── gestionericerca/   — Search functionality
│   │   ├── gestioneprenotazioni/ — Bookings
│   │   ├── gestioneitinerari/ — Itinerary planning (incl. AI adapter)
│   │   └── ...
│   ├── src/test/
│   └── pom.xml
├── frontend/
│   ├── src/
│   └── package.json
├── docker-compose*.yml
└── docs/
```

## Key Commands (Backend)

| Command | Description |
|---|---|
| `./mvnw test` | Run tests |
| `./mvnw verify` | Run tests + JaCoCo coverage + Pitest mutation tests |
| `./mvnw checkstyle:check` | Code style check |
| `./mvnw pitest:mutationCoverage` | Mutation testing only |
| `./mvnw compile` | Compile |

## Key Commands (Frontend)

| Command | Description |
|---|---|
| `npm start` | Dev server (`ng serve`) |
| `npm run build` | Production build |
| `npm test` | Run Karma tests |
| `npm run watch` | Watch mode |

## Testing Conventions

- Backend: JUnit 5, Mockito, JaCoCo (coverage), Pitest (mutation), JMH (benchmarks)
- Frontend: Karma + Jasmine
- JaCoCo excludes: entities, enums, exceptions, certain utils and config classes
- Pitest excludes: entities, enums, security config, backend application, AI adapter (temporarily)

## Code Style

- Google Java Style (via `maven-checkstyle-plugin` with `google_checks.xml`)
- Suppressions in `backend/checkstyle-suppressions.xml`
- Lombok is used (getters, setters, constructors, builders)

## Notes for Agents

- `backend/AGENT.md` contains a specific JMH benchmark implementation guide.
- The AI adapter (`ItinerariAiAdapter`) is temporarily excluded from tests.
- For any architectural decisions, refer to existing patterns in the codebase.
- `.env` file is gitignored — check `docker-compose.yml` for required environment variables.
