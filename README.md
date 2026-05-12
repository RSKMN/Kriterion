# Kriterion

Kriterion is a sophisticated personal finance management and behavioral analysis platform. It combines traditional budgeting with advanced cognitive reflection to provide users with a deep understanding of their financial habits through data-driven insights.

## **Core Pillars**

### **1. Financial Management**
- **Unified Tracking**: Seamless management of income, expenses, and balances.
- **Categorization Engine**: Automated and manual categorization of transactions with custom tagging.
- **Budgeting Controls**: Set and monitor budget thresholds with real-time alerts.
- **Recurring Transactions**: Intelligent detection and management of fixed obligations.

### **2. Behavioral Intelligence**
- **Financial Cognitive Reflection**: A research-oriented module that provides a "behavioral mirror" of spending rhythms and temporal patterns.
- **Predictive Analytics Lite**: Heuristic-based forecasting for future spending trends and potential overspending risks.
- **Pattern Recognition**: Identification of spending bursts, late-night activity, and structural shifts in financial behavior.
- **Cognitive Load Analysis**: Measuring the complexity and frequency of financial decision-making.

### **3. Security & Integrity**
- **JWT Authentication**: Secure, stateless authentication with robust token management.
- **Data Isolation**: Strict user-scoped data protection ensuring privacy and integrity.
- **Database Migrations**: Version-controlled schema evolution using Flyway.

## **Tech Stack**

- **Backend**: Java 21, Spring Boot 3.4, Spring Security, JPA/Hibernate, MySQL.
- **Frontend**: React, TypeScript, Tailwind CSS, shadcn/ui, Lucide, Recharts, Axios.
- **Infrastructure**: Docker & Docker Compose for orchestrated containerization.

## **Project Structure**

- `code/backend`: Spring Boot API server, behavioral engines, and data persistence.
- `code/frontend`: Modern React application with feature-based architecture.
- `code/docker`: Orchestration configurations for local development and deployment.

## **Quick Start**

The easiest way to run the entire Kriterion ecosystem is using Docker:

```bash
cd code/docker
docker compose up --build
```

Access the application:
- **Frontend**: `http://localhost:5173`
- **Backend API**: `http://localhost:8080/api/v1`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## **Development Setup**

### **Backend**
```bash
cd code/backend
mvn clean spring-boot:run
```

### **Frontend**
```bash
cd code/frontend
npm install
npm run dev
```

---

*Kriterion is built with a focus on privacy and analytical accuracy, avoiding emotional AI in favor of objective behavioral patterns.*
