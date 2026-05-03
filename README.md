# 🚀 TaskNode

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D.svg)

**TaskNode** is a robust and scalable internal task management API backend designed to orchestrate workflows, manage team collaborations, and track project progress effectively. Built with Java 17 and Spring Boot, it features real-time communications, secure authentication, and advanced reporting capabilities.

## ✨ Key Features

- **🔐 Authentication & Security**: Secure JWT-based authentication with refresh token mechanism. Role-based access control (Admin, Manager, Staff).
- **📋 Task Orchestration**: Comprehensive CRUD operations for tasks. Assign, update status, track personal progress, and handle file attachments.
- **✅ Approval Workflow**: Built-in mechanisms for managers to approve or reject completed tasks with feedback.
- **🏢 Structure Management**: Easily manage Departments, Projects, and Users (Team Members).
- **💬 Real-time Collaboration**: WebSocket-powered live notifications and task-specific chat/comments.
- **📊 Reporting & Exporting**: Generate and export project progress and employee performance reports in Excel (Apache POI) and PDF (iTextPDF) formats.
- **📖 API Documentation**: Auto-generated interactive API docs using Swagger / OpenAPI 3.

## 🛠️ Technology Stack

- **Core**: Java 17, Spring Boot 4.0.5, Spring WebMVC
- **Data Access**: Spring Data JPA, Hibernate, MySQL Connector
- **Security**: Spring Security, JJWT (JSON Web Token)
- **Real-time**: Spring WebSocket
- **Utilities**: Lombok, MapStruct, Java-Dotenv, Commons IO
- **Reporting**: Apache POI (Excel), iTextPDF (PDF)
- **Documentation**: Springdoc OpenAPI (Swagger UI)

## ⚙️ Getting Started

### Prerequisites

- Java 17 Development Kit (JDK)
- Maven 3.8+
- MySQL Server 8.0+

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd TaskNode
   ```

2. **Configure Environment Variables:**
   Create a `.env` file in the root directory based on the following template:
   ```env
   # Server
   SERVER_PORT=8080

   # Database
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=tasknode
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password

   # JWT Config
   JWT_SECRET=your_super_secret_jwt_key_that_is_long_enough
   JWT_EXPIRATION=86400000
   JWT_REFRESH_EXPIRATION=604800000

   # Admin Seed
   ADMIN_USERNAME=admin
   ADMIN_EMAIL=admin@tasknode.com
   ADMIN_PASSWORD=adminpassword
   ADMIN_FULLNAME="System Administrator"
   ```

3. **Build the project:**
   ```bash
   mvn clean install -DskipTests
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   The server will start on `http://localhost:8080`.

## 📚 API Documentation

Once the application is running, you can explore and test the RESTful endpoints via the interactive Swagger UI:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

For a detailed breakdown of the API architecture and endpoints, refer to the [`api.md`](./api.md) document.

## 📐 Architecture & Guidelines

This project strictly adheres to a standard layered Spring Boot architecture:
- `Controller` -> `Service` -> `Repository` -> `Database`
- DTOs and MapStruct are used exclusively for data transfer, never exposing direct Entity models to the API payload.

For complete contribution guidelines, coding standards, and system constraints, please read the [`CLAUDE.md`](./CLAUDE.md) and [`RULE.md`](./RULE.md) files.
