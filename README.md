# Spring Boot URL Shortener Backend

**Project Repository URL**: https://github.com/Prutwi17/url-shortener

A clean, lightweight, production-ready RESTful URL Shortener backend API built with Java 17 and Spring Boot 3.3.5.

## Features

- **Shorten URL (`POST /shorten`)**: Converts long HTTP/HTTPS URLs into unique 6-character short codes.
- **Get URL Info (`GET /shorten/{shortCode}`)**: Retrieves metadata for a shortened URL.
- **Update URL (`PUT /shorten/{shortCode}`)**: Updates the target URL while preserving `id`, `shortCode`, and `createdAt`.
- **Delete URL (`DELETE /shorten/{shortCode}`)**: Removes a shortened URL.
- **URL Statistics (`GET /shorten/{shortCode}/stats`)**: Retrieves metadata along with access count (`accessCount`).
- **HTTP 302 Redirect (`GET /{shortCode}`)**: Redirects short URL requests to the original URL and increments `accessCount` by exactly 1 per redirect.
- **URL Format Validation**: Enforces valid `http://` and `https://` schemas.
- **Global Exception Handling**: Returns clean, consistent JSON error payloads.

---

## Tech Stack

- **Java**: 17
- **Framework**: Spring Boot 3.3.5
- **Build Tool**: Maven
- **Database**: MySQL 8+ (Runtime), H2 (In-memory Test Scope)
- **Persistence**: Spring Data JPA / Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
- **Validation**: Jakarta Validation (`@Valid`, `@NotBlank`, custom `@ValidUrl`)
- **Testing**: JUnit 5, Mockito, Spring Boot MockMvc Integration Tests

---

## Configuration & Database Setup

The application connects to a MySQL database named `url_shortener_db`.

### `application.properties`

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/url_shortener_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

---

## Building and Running

### Build Project & Package JAR

```bash
.\mvnw.cmd clean package
```

### Run Automated Unit & Integration Tests

```bash
.\mvnw.cmd clean test
```

### Run Application

```bash
.\mvnw.cmd spring-boot:run
```
Or run the packaged JAR:
```bash
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

---

## API Endpoints Overview

| Method | Endpoint | Description | Response Status |
|--------|----------|-------------|-----------------|
| `POST` | `/shorten` | Create short URL | `201 Created` |
| `GET` | `/shorten/{shortCode}` | Retrieve short URL info | `200 OK` / `404 Not Found` |
| `PUT` | `/shorten/{shortCode}` | Update target URL | `200 OK` / `400 Bad Request` / `404 Not Found` |
| `DELETE` | `/shorten/{shortCode}` | Delete short URL | `204 No Content` / `404 Not Found` |
| `GET` | `/shorten/{shortCode}/stats` | Retrieve URL stats & access count | `200 OK` / `404 Not Found` |
| `GET` | `/{shortCode}` | Redirect to original URL | `302 Found` / `404 Not Found` |

---

## Sample Request & Response Payloads

### 1. Create Short URL (`POST /shorten`)

**Request**:
```json
POST /shorten
Content-Type: application/json

{
  "url": "https://example.com/very/long/url"
}
```

**Response (`201 Created`)**:
```json
{
  "id": 1,
  "url": "https://example.com/very/long/url",
  "shortCode": "aB3xD9",
  "createdAt": "2026-09-17T02:35:00",
  "updatedAt": "2026-09-17T02:35:00"
}
```

---

### 2. Retrieve URL Information (`GET /shorten/{shortCode}`)

**Response (`200 OK`)**:
```json
{
  "id": 1,
  "url": "https://example.com/very/long/url",
  "shortCode": "aB3xD9",
  "createdAt": "2026-09-17T02:35:00",
  "updatedAt": "2026-09-17T02:35:00"
}
```

---

### 3. Redirect to Original URL (`GET /{shortCode}`)

**Response (`302 Found`)**:
```http
HTTP/1.1 302 Found
Location: https://example.com/very/long/url
```

---

### 4. Retrieve Statistics (`GET /shorten/{shortCode}/stats`)

**Response (`200 OK`)**:
```json
{
  "id": 1,
  "url": "https://example.com/very/long/url",
  "shortCode": "aB3xD9",
  "createdAt": "2026-09-17T02:35:00",
  "updatedAt": "2026-09-17T02:35:00",
  "accessCount": 1
}
```

---

### 5. Update Target URL (`PUT /shorten/{shortCode}`)

**Request**:
```json
PUT /shorten/aB3xD9
Content-Type: application/json

{
  "url": "https://example.com/updated/url"
}
```

**Response (`200 OK`)**:
```json
{
  "id": 1,
  "url": "https://example.com/updated/url",
  "shortCode": "aB3xD9",
  "createdAt": "2026-09-17T02:35:00",
  "updatedAt": "2026-09-17T02:36:12"
}
```

---

### 6. Delete Short URL (`DELETE /shorten/{shortCode}`)

**Response (`204 No Content`)**

---

### Error Responses Format (`400 Bad Request` / `404 Not Found`)

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Short URL not found for code: invalidCode",
  "timestamp": "2026-09-17T02:37:00"
}
```
