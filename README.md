
Tax Gap Detection & Compliance Validation Service

A Spring Boot–based backend service that processes financial transactions, validates reported tax against expected tax, detects compliance gaps, stores exceptions, and generates optimized reports.

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
spring.datasource.username=root
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

🌐 Sample API Calls (cURL / Postman)
✅ 1. Upload Transactions
Endpoint
POST /api/transactions/upload
Sample JSON (Bulk Upload)
[
  {
    "customerId": "CUST1001",
    "amount": 10000.00,
    "reportedTax": 1500.00,
    "expectedTax": 1800.00
  },
  {
    "customerId": "CUST1002",
    "amount": 5000.00,
    "reportedTax": 900.00,
    "expectedTax": 900.00
  }
]
Sample curl
curl -X POST http://localhost:8080/api/transactions/upload \
-H "Content-Type: application/json" \
-d @transactions.json
✅ 2. Customer Tax Summary Report
Endpoint
GET /api/reports/customer-tax-summary
Sample curl
curl http://localhost:8080/api/reports/customer-tax-summary
✅ 3. Total Exception Count
Endpoint
GET /api/reports/exception-summary/total
Sample curl
curl http://localhost:8080/api/reports/exception-summary/total
✅ 4. Exceptions by Severity
Endpoint
GET /api/reports/exception-summary/by-severity
Sample curl
curl http://localhost:8080/api/reports/exception-summary/by-severity
✅ 5. Exceptions by Customer
Endpoint
GET /api/reports/exception-summary/by-customer
Sample curl
curl http://localhost:8080/api/reports/exception-summary/by-customer
📌 Notes

All reporting APIs use database-level aggregation (GROUP BY, SUM, COUNT).

No in-memory processing is used for report generation.

Indexed columns improve performance for large datasets.

Designed for scalability and production use.
