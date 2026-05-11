# Kriterion Manual Test Plan

This document outlines the manual test cases required to verify the functionality of the Kriterion Personal Finance application.

## 1. Application Setup & Initialization

### Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Maven 3.8+

### Execution Commands

#### Option A: Manual Setup (Local Development)

**Backend Server:**

```bash
cd code/backend
./mvnw spring-boot:run
```

**Frontend Application:**

```bash
cd code/frontend
npm install
npm run dev
```

#### Option B: Docker Setup (Verified)

Ensure you have Docker and Docker Compose installed.

**Run all services:**

```bash
cd code/docker
docker-compose up --build
```

- **Backend API:** http://localhost:8080/api/v1
- **Frontend App:** http://localhost:5173
- **Database:** MySQL on port 3306

> [!NOTE]
> Several compilation and configuration issues (missing migrations, bean conflicts, and ambiguous imports) have been resolved to ensure a smooth Docker build.

---

## 2. Authentication & User Access

### TC 2.1: User Registration

- **Steps:**
  1. Navigate to `/register`.
  2. Enter a unique username, email, and strong password.
  3. Click "Sign Up".
- **Expected Output:** User is redirected to the login page with a success message.

### TC 2.2: User Login

- **Steps:**
  1. Navigate to `/login`.
  2. Enter valid credentials.
  3. Click "Login".
- **Expected Output:** User is redirected to the Dashboard. JWT is stored in local storage.

### TC 2.3: Protected Routes

- **Steps:**
  1. Log out.
  2. Try to access `/dashboard` or `/transactions` directly via URL.
- **Expected Output:** User is redirected to the login page.

---

## 3. Dashboard & Analytics

### TC 3.1: Data Accuracy on Summary Cards

- **Steps:**
  1. View the Dashboard.
  2. Compare "Total Balance", "Monthly Income", and "Monthly Expenses" with the sum of transactions.
- **Expected Output:** Cards display correct totals and percentage changes.

### TC 3.2: Visualization (Charts)

- **Steps:**
  1. Ensure there are transactions for the current month.
  2. Check the Spending Trend line chart and Category breakdown pie chart.
- **Expected Output:** Charts render correctly with tooltips showing transaction data.

---

## 4. Transaction Management

### TC 4.1: Create Transaction

- **Steps:**
  1. Navigate to `/transactions`.
  2. Click "Add Transaction".
  3. Fill in title, amount, category, date, and type (Income/Expense).
  4. Save.
- **Expected Output:** Transaction appears in the list; dashboard totals update.

### TC 4.2: Edit/Update Transaction

- **Steps:**
  1. Click on an existing transaction.
  2. Modify the amount or category.
  3. Save changes.
- **Expected Output:** List updates immediately with new values.

### TC 4.3: Server-side Filtering & Search

- **Steps:**
  1. Use the filter bar to search by merchant name.
  2. Filter by date range or category.
- **Expected Output:** The table updates to show only matching transactions (verified via network tab for API calls).

---

## 5. OCR Receipt Scanning

### TC 5.1: Receipt Upload & Extraction

- **Steps:**
  1. Navigate to `/receipts`.
  2. Upload an image of a receipt (JPG/PNG).
  3. Wait for OCR processing.
- **Expected Output:** "Extracted Data Form" is populated with Merchant Name, Amount, and Date.

### TC 5.2: Verification Workflow

- **Steps:**
  1. Review the side-by-side preview of the receipt and extracted data.
  2. Manually correct an extracted value if necessary.
  3. Click "Confirm & Save".
- **Expected Output:** Transaction is saved to the database and appears in the transaction history.

---

## 6. Recurring Transactions & Reminders

### TC 6.1: Schedule Recurring Transaction

- **Steps:**
  1. Navigate to `/recurring`.
  2. Create a new schedule (e.g., Monthly Rent).
  3. Set the next due date to tomorrow.
- **Expected Output:** Schedule is saved; status shows as "Active".

### TC 6.2: Due Date Warnings

- **Steps:**
  1. View the Dashboard widget for upcoming transactions.
- **Expected Output:** The transaction scheduled for tomorrow appears with an "Upcoming" badge (likely orange/yellow).

---

## 7. Reports & Exports

### TC 7.1: CSV Transaction Export

- **Steps:**
  1. Navigate to `/reports`.
  2. Select a date range.
  3. Click "Export CSV".
- **Expected Output:** A `.csv` file downloads containing the filtered transactions.

### TC 7.2: PDF Monthly Report

- **Steps:**
  1. Click "Download PDF" under Monthly Report.
- **Expected Output:** A PDF report is generated containing summaries, charts, and a category breakdown for the current month.

### TC 7.3: Full JSON Backup

- **Steps:**
  1. Click "Generate JSON Backup".
- **Expected Output:** A `.json` file containing all user data (transactions, categories, etc.) is downloaded.

---

## 8. Category Management

### TC 8.1: Create & Delete Category

- **Steps:**
  1. Navigate to `/categories`.
  2. Add a new custom category with an icon/color.
  3. Delete a category that has no transactions.
- **Expected Output:** Category list updates correctly.

---

## 9. Notifications

### TC 9.1: System Alerts

- **Steps:**
  1. Trigger an event (e.g., successful export or budget limit reached).
  2. Check the Notifications bell icon or `/notifications`.
- **Expected Output:** Notification appears with correct message and timestamp.
