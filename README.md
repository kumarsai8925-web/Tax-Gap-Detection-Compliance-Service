
Tax Gap Detection & Compliance Validation Service

A Spring Boot–based backend service that processes financial transactions, validates reported tax against expected tax, detects compliance gaps, stores exceptions, and generates optimized reports.

The project is a Tax Gap Detection and Compliance Validation Service built using Spring Boot with a layered architecture.

At a high level, the system processes financial transactions, validates reported tax against expected tax, identifies compliance gaps, stores exceptions, and generates customer-level compliance reports.

We follow a clean layered architecture:

Controller → Service → Repository → Database.

The Controller layer exposes REST APIs for transaction upload and reporting.
The Service layer contains the core business logic like tax computation, compliance determination, and rule execution.
The Repository layer uses Spring Data JPA with JPQL constructor projections for optimized reporting queries.
The Database layer stores transactions and exceptions, with proper indexing for performance.

When a transaction is uploaded:

It is validated and persisted.

Business rules are executed.

The system computes expected tax and tax gap.

Compliance status is determined.

Any violations are stored as structured exceptions.

Each stage can be audit logged.

For reporting, we use database-level aggregation with GROUP BY, SUM, and COUNT instead of in-memory processing. This ensures scalability for large datasets.

The system is designed to be modular, so new validation rules can be added easily without changing core logic.

Overall, the design ensures clean separation of concerns, performance efficiency, and production-ready scalability.”

🚀 How to Run the Application

1️⃣ Prerequisites

Java 17+

Maven 3.8+

MySQL 8+ (or PostgreSQL if configured)

IDE (IntelliJ / Eclipse / VS Code)

2️⃣ Clone the Repository
git clone <your-repository-url>
cd tax-gap-detection
3️⃣ Configure Database

Update application.properties (or application.yml) with your DB credentials.

Example (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/tax_gap_db
spring.datasource.username=
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

4️⃣ Build & Run the Application
mvn clean install
mvn spring-boot:run

Application will start at:

http://localhost:8080
🗄 Database Setup Instructions

Step 1: Create Database

For MySQL:

CREATE DATABASE tax_gap_db;

For PostgreSQL:

CREATE DATABASE tax_gap_db;

Step 2: Table Creation

Tables are automatically created by Hibernate if:

spring.jpa.hibernate.ddl-auto=update

Tables created:

transactions

transaction_exception

🌐 Sample API Calls (Postman)
✅ 1. Upload Transactions
Endpoint
POST /api/transactions/upload

Sample JSON (Bulk Upload)
[
  {
    "transactionId": "TXN1001",
    "date": "2026-03-04",
    "customerId": "CUST01",
    "amount": 10000,
    "taxRate": 0.18,
    "reportedTax": 1800,
    "transactionType": "SALE"
  },
  {
    "transactionId": "TXN1002",
    "date": "2026-03-04",
    "customerId": "CUST02",
    "amount": 10000,
    "taxRate": 0.18,
    "reportedTax": 1000,
    "transactionType": "SALE"
  },
  {
    "transactionId": "TXN1003",
    "date": "2026-03-04",
    "customerId": "CUST03",
    "amount": 5000,
    "taxRate": 0.18,
    "reportedTax": 1500,
    "transactionType": "SALE"
  },
  {
    "transactionId": "TXN1004",
    "date": "2026-03-04",
    "customerId": "CUST04",
    "amount": 3000,
    "originalSaleAmount": 2000,
    "taxRate": 0.18,
    "reportedTax": 540,
    "transactionType": "REFUND"
  },
  {
    "transactionId": "TXN1005",
    "date": "2026-03-04",
    "customerId": "CUST05",
    "amount": 2000,
    "taxRate": 0.18,
    "reportedTax": 360,
    "transactionType": "REFUND"
  },
  {
    "transactionId": "TXN1006",
    "date": "2026-03-04",
    "customerId": "CUST06",
    "amount": 100000,
    "taxRate": 0.18,
    "reportedTax": 1000,
    "transactionType": "SALE"
  },
  {
    "transactionId": "TXN1007",
    "date": "invalid-date",
    "customerId": "CUST07",
    "amount": -100,
    "transactionType": "SALE"
  }
]



✅ 2. Customer Tax Summary Report
Endpoint
GET /api/reports/customer-tax-summary

✅ 3. Total Exception Count
Endpoint
GET /api/reports/exception-summary/total

✅ 4. Exceptions by Severity
Endpoint
GET /api/reports/exception-summary/by-severity

✅ 5. Exceptions by Customer
Endpoint
GET /api/reports/exception-summary/by-customer

📌 Notes

All reporting APIs use database-level aggregation (GROUP BY, SUM, COUNT).
No in-memory processing is used for report generation.
Indexed columns improve performance for large datasets.
Designed for scalability and production use.
