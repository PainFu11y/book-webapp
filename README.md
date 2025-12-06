# Java Spring Boot Book Store Project

This project is a **Book Store Service** implemented using **Java 17** and **Spring Boot 3**, demonstrating a full-stack backend application with best practices and modular architecture.

The project covers:

- **MVC architecture** with clear separation of concerns.
- **Spring Data JPA** for database operations with PostgreSQL.
- **Spring Security** for authentication and authorization.
- **JWT-based login** and **role-based access control (RBAC)** with roles: ADMIN, EMPLOYEE, CUSTOMER.
- **Session timeouts and limits** (for non-JWT implementations).
- **URL-based security** using SecurityFilterChain.
- **Custom security expressions** (e.g., checking ownership of an entity).
- **Refresh tokens** support for long-lived authentication sessions.
- **Employee functionalities**: managing books, orders, and blocking/unblocking customers.
- **Customer functionalities**: browsing books, placing orders, managing personal accounts.
- **Admin functionalities**: full access to all system resources, including managing employees, clients, books, and orders.
- **Global exception handling** via `@ControllerAdvice` for consistent error responses.
- **Logging** implemented using AOP (Aspect-Oriented Programming) to track important operations.
- **Swagger** integration for API documentation and testing.
- **DTOs, validation, and error handling** for robust API responses.
- **Password hashing using BCrypt** (`BCryptPasswordEncoder`).
- **Lombok** for boilerplate reduction and **ModelMapper** for DTO mapping.
- **Redis integration** for temporary login attempt tracking (brute-force protection).
- **Data validation** for incoming requests using `@Valid` and custom validators.

You can jump directly to the project details here: [Book Store Project](#book-store-spring-project)

---

## .env example

```dotenv
# Server configuration
SERVER_PORT=8084

# JWT configuration
JWT_SECRET=uQфва
JWT_EXPIRATION_MS=86400000  # 24 hours in milliseconds

# OAuth2 (Google) configuration
GOOGLE_CLIENT_ID=6фвыаыфваpps.googleusercontent.com
GOOGLE_CLIENT_SECRET=фываыфваыфв3LVjQ

# Email configuration
MAIL_USERNAME=emailveify@gmail.com
MAIL_PASSWORD=xвфыавыфафww

# Redis configuration
REDIS_HOST=localhost
REDIS_PORT=6379

```

---


## Setup & Run

### 1. Prerequisites

Make sure you have installed:

- **Java 17** or higher
- **Maven 3.8+**
- **H2** database
- **Redis** server
- Optional: **Docker** (for PostgreSQL and Redis containers)

---
### 2. Clone the repository

```bash
git clone https://github.com/your-username/bookstore-spring-project.git
cd bookstore-spring-project
```
---
### 3.Build and run the project
mvn clean install
mvn spring-boot:run
---
### 4.Access Swagger UI
```
http://localhost:8084/swagger-ui/index.html
```
![swagger](readme-images/img_1.png)
---
### 5.Access H2 db
```
http://localhost:8084/h2-console/
```
![h2-login](readme-images/img.png)

---

### Forgot Password Endpoint
**POST** `/auth/forgot-password`

![forgot-password](readme-images/img_2.png)

---

## Project Limitations

1. **Redis usage**
  - Redis is currently only used for tracking failed login attempts.
  - No caching of other entities (books, clients, orders) implemented.

2. **Frontend**
  - No frontend is provided; the project exposes REST endpoints only.
  - Testing must be done via Swagger UI, Postman, or custom clients.
    
3. **Order and inventory management**
  - Orders are created and stored but no advanced inventory management is implemented.
  - Order status tracking is basic; no detailed history or shipment tracking implemented.
  - No advanced inventory management is implemented (e.g., stock levels, automatic stock deduction, or notifications for low stock).  
  - No payment gateway integration.

4. **Security**
  - Role-based access is functional, but finer-grained permissions (e.g., per-book ownership) are not fully implemented.
  - Role-based access is functional, but **multi-role support for a single user is not implemented**.


> I hope these limitations are considered acceptable for a 15-hour task/project demonstration.

# Book Store. Spring Project


The purpose of this task is to check your knowledge and understanding in Java and Spring.

Duration: **15** hours

## Description

Your objective is to develop a "Book Store Service" following the MVC pattern.

> Project may have two main roles of authority: customer and employee.

The project structure is already set up, with essential classes waiting for implementation in their respective folders.
Your project is organized into several packages. Here's a brief overview of each:

### Packages Overview

#### `conf`

- Houses all configuration classes.

#### `controller`

- Contains controller files.

#### `dto`

- Contains DTO files.

#### `model`

- Contains all model classes.

#### `exception`

- Contains custom user exception files.

#### `repo`

- Contains repository files.

#### `service`

- Includes interfaces with declared methods for all services.

- `impl`: Encompasses implementations of declared services.

The class diagram of the Domain model is shown in the figure below:

<img src="img/Diagram.png" alt="DTO" width="1000"/>

### Permissions

> For Any Registered Users

- Access a list of available books.
- View detailed information about any book.
- Edit personal information and view user profile.

> For Employees

- Add, edit, or delete books from the list.
- Confirm orders placed by customers.
- Block or unblock customer accounts.
- Access a list of registered customers.

> For Customers

- Add books to the basket for purchase.
- Delete their account.
- Submit orders for purchase.

### Services

Below is a list of available services with corresponding methods for implementation.

> Note: You can add your own methods to existing services, as well as create additional services.

#### OrderService

* `getAllOrdersByClient(email: String)`
  Retrieves a list of all orders by client's email placed in the system.
* `getAllOrdersByEmployee(email: String)`
  Retrieves a list of all orders by employee's email placed in the system.
* `addOrder(order: OrderDTO)`
  Adds a new order to the system, incorporating the provided order details.

#### EmployeeService

* `getAllEmployees()`
  Retrieves a list of all employees registered in the system.
* `getEmployeeByEmail(email: String)`
  Fetches details of a specific employee based on their email.
* `updateEmployeeByEmail(email: String, employee: EmployeeDTO)`
  Updates the information of an existing employee identified by their email with the provided details.
* `deleteEmployeeByEmail(email: String)`
  Removes an employee from the system based on their email.
* `addEmployee(employee: EmployeeDTO)`
  Registers a new employee in the system with the provided details.

#### ClientService

* `getAllClients()`
  Retrieves a list of all clients (customers) registered in the system.
* `getClientByEmail(email: String)`
  Fetches details of a specific client based on their email.
* `updateClientByEmail(email: String, client: ClientDTO)`
  Updates the information of an existing client identified by their email with the provided details.
* `deleteClientByEmail(email: String)`
  Removes a client from the system based on their email.
* `addClient(client: ClientDTO)`
  Registers a new client in the system with the provided details.

#### BookService

* `getAllBooks()`
  Retrieves a list of all books available in the store.
* `getBookByName(name: String)`
  Fetches details of a specific book based on its name.
* `updateBookByName(name: String, book: BookDTO)`
  Updates the information of an existing book identified by its name with the provided details.
* `deleteBookByName(name: String)`
  Removes a book from the system based on its name.
* `addBook(book: BookDTO)`
  Adds a new book to the system with the provided details.

## Requirements

Ensure implementation of the following:

- `Spring Data JPA` for efficient data management.
-  Incorporate `Spring Security` for robust authentication and authorization.
-  Enable `Internationalization and Localization` to support English and any language you choose.
-  Implement `Validation` for data integrity.
-  Establish `Error handling` for graceful error management.
-  Utilize `DTOs` - data transfer objects structured as illustrated below:

<img src="img/DTO.png" alt="DTO" width="600"/>

## Would be nice

Consider the following additional features:

- Incorporate `Logging` for comprehensive system monitoring.
- Implement `Pagination and Sorting` for enhanced data presentation.

## Recommendations

> Use wrapper classes (like Long, Integer, etc.) instead of primitive types whenever possible.

- Utilize `Lombok` for streamlined Java code.
- Use `ModelMapper` for easy mapping between objects.
- Utilize `Thymeleaf` for HTML templating.
- Explore the `test` folder to execute provided test cases for your solution.
- Refer to the `main\resources\sql` folder for SQL scripts to initialize data.

## Special message

- Make the most of the time available.
  While we understand you may not cover all the points,
  aim to accomplish as much as possible within the given duration of 15 hours.
