# Pastebin-like Application

This workspace contains a small Pastebin-like application with a Spring Boot backend and a React frontend.

Overview
- Backend: Spring Boot (Java), stores pastes in file-based H2 (jdbc:h2:file:./data/paste-db) so data persists across requests and restarts for local/dev usage.
- Frontend: React app (in `frontend/`) that calls backend API.

Key endpoints
- `GET /api/healthz` - health check, returns JSON {"ok": true}
- `POST /api/pastes` - create a paste; JSON body: `{ "content": "...", "ttl_seconds": 60, "max_views": 5 }`
- `GET /api/pastes/:id` - fetch paste as JSON; a successful fetch consumes 1 view
- `GET /p/:id` - view paste as HTML (renders content safely)

Deterministic testing
If environment variable `TEST_MODE=1` then the request header `x-test-now-ms` (milliseconds since epoch) is used as "now" for expiry logic.

Persistence choice
This implementation uses H2 file-based database (`jdbc:h2:file:./data/paste-db`) for simplicity and persistence across requests. For production or deployed graders, use a managed Postgres/Redis/KV service and update `spring.datasource.url` accordingly.

Run backend (from repository root)

```bash
cd backend
mvn spring-boot:run
```

Run frontend

```bash
cd frontend
npm install
npm start
```

Build frontend for production and serve with backend

- Build the frontend (`npm run build`) and copy the `build` output into backend static resources or configure a reverse proxy. This repo keeps frontend separate by default.
