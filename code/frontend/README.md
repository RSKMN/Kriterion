# Frontend (React + Vite + TypeScript)

This folder contains the Vite React frontend. Feature-based organization is used: `features/` groups related UI and logic. Reusable UI components live under `components/ui`.

Key folders:

- `src/components` — reusable presentational components
- `src/features` — feature modules (auth, expenses, dashboard, etc.)
- `src/services` — API wrappers and integrations (OCR, AI)
- `src/store` — global state (Zustand)
- `src/hooks` — custom hooks
- `src/lib` — infra like PWA, analytics
