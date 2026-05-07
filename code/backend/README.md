# Backend (Spring Boot)

Layered architecture with clear separation of concerns:

- `controller` — REST controllers / API endpoints
- `service` — business logic
- `repository` — Spring Data JPA interfaces
- `entity` — JPA entities
- `dto` — Data transfer objects
- `config` — application configuration, Swagger
- `security` — JWT and Spring Security setup
- `ocr` — OCR integration adapters
- `ai` — AI categorization adapters

Use Flyway or Liquibase for DB migrations in production.
