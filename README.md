# Kriterion

A scalable personal budget analysis and decision support system.

## What it does

- Tracks income, expenses, categories, and balances.
- Provides dashboard analytics with summary cards, charts, and insights.
- Supports authenticated, user-scoped analytics data.
- Uses a Spring Boot backend and a React + TypeScript frontend.

## Dashboard features

- Summary cards for total balance, income, expenses, and savings.
- Recharts-based spending visualizations.
- Top categories and weekly spending insights.
- Loading, error, and empty states for a production-style UI.

## Tech stack

- Backend: Spring Boot, JWT authentication, JPA/Hibernate.
- Frontend: React, TypeScript, Tailwind CSS, shadcn/ui, Recharts, Axios.

## Project structure

- `code/backend` — API server and analytics endpoints.
- `code/frontend` — dashboard UI, charts, and analytics experience.
- `docs` — architecture and API documentation.

## Frontend scripts

From `code/frontend`:

```bash
npm install
npm run dev
npm run type-check
npm run lint
```

## Backend scripts

From `code/backend`:

```bash
./mvnw spring-boot:run
./mvnw test
```

## Notes

- The dashboard uses existing analytics APIs.
- Charts are responsive and theme-aware.
- Insight text is derived from recent analytics data, not AI-generated.
