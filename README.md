# GreenTrails

Portale per la creazione e la prenotazione di itinerari ecosostenibili.

Fork del [progetto originale](https://github.com/GerardoFesta/GreenTrails) di Gestione dei Progetti Software/Ingegneria del Software, a.a. 2023/2024, Università degli Studi di Salerno.

Questo fork è parte di tre differenti progetti (a.a. 2025/2026, Università degli Studi di Salerno):
- **Ingegneria del Software: Tecniche Avanzate**, per manutenzione evolutiva dell'applicativo;
- **Software Dependability**, per manutenzione perfettiva in ambito di affidabilità e sicurezza;
- **Sustainable Software Engineering**, per manutenzione perfettiva in ambito di sostenibilità ambientale, sociale ed economica.

## Partecipanti

Di seguito, sono elencati i partecipanti per ogni progetto precedentemente citato:

|     Team Member     | ISTA | SD | SSE |
|:-------------------:|:----:|:--:|:---:|
| Gabriele Di Stefano |  x   | x  |  x  |
|  Roberta Galluzzo   |  x   | x  |  x  |

## Avvio rapido

### Prerequisiti

- Java 21 (backend)
- Bun (frontend)
- Docker e Docker Compose (opzionale, per ambiente containerizzato)

### Esecuzione con Docker

```bash
# Avvia tutti i servizi (backend, frontend, database)
docker compose up -d

# Per ambiente di produzione
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Esecuzione bare metal

```bash
# Backend
cd backend
./mvnw spring-boot:run

# Frontend (in un altro terminale)
cd frontend
bun install
bun run dev
```

### Setup pre-commit hooks

Il progetto utilizza [pre-commit](https://pre-commit.com) per eseguire
controlli automatici prima di ogni commit, tra cui secret scanning
tramite [gitleaks](https://github.com/gitleaks/gitleaks).

```bash
# Installa pre-commit (se non già presente)
pip install pre-commit   # o: brew install pre-commit

# Attiva gli hooks nel repository
pre-commit install
```

Dopo l'installazione, ogni `git commit` eseguirà automaticamente:
- `end-of-file-fixer` — assicura newline a fine file
- `trailing-whitespace` — rimuove spazi bianchi finali
- `check-yaml` — valida la sintassi YAML
- `check-added-large-files` — blocca file oltre 512 KB
- `gitleaks` — scansiona i file staged per segreti (API key,
  password, token, ecc.)

Per eseguire gli hooks manualmente su tutti i file:

```bash
pre-commit run --all-files
```
