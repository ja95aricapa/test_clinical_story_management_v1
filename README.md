# Clinical History Management System

Spring Boot + Thymeleaf application to manage **Patients** and their **Clinical Records**, backed by **MySQL**.  
The project is designed to run **the same way locally and in Docker**, using a single `application.properties` with environment-variable overrides.

---

## Prerequisites

### Recommended (run with Docker)

- Docker Engine
- Docker Compose (plugin)

Verify:

```bash
docker --version
docker compose version
```

> If your system only has legacy `docker-compose` (python v1), see **Troubleshooting**.

### Local development (without Docker)

- Java **17+**
- Maven **3.8+**
- MySQL **8.0+**
- (Optional) DBeaver / TablePlus / MySQL Workbench

Verify:

```bash
java -version
mvn -version
mysql --version
```

---

## Deploy with one command (Docker Compose)

From the project root:

```bash
docker compose up --build
```

This starts:

- `db` → MySQL 8 (database `clinical_db`)
- `app` → Spring Boot app (Tomcat embedded) on port `8080`

### URLs

- **App UI**
  - Patients: [http://localhost:8080/patients](http://localhost:8080/patients)
  - Records: [http://localhost:8080/records](http://localhost:8080/records)

- **Database (MySQL)**
  - Host: `localhost`
  - Port: `3306`
  - DB: `clinical_db`
  - User: `root`
  - Password: `root`

### Stop / clean up

Stop containers:

```bash
docker compose down
```

Stop and delete DB data:

```bash
docker compose down -v
```

---

## How to test functionality (UI)

Open:

- [http://localhost:8080/patients](http://localhost:8080/patients)

### Suggested test flow (end-to-end)

1. **Create a patient**
   - Click **Add Patient**
   - Fill:
     - `Document ID` (must be unique)
     - `Full Name`
     - `Birth Date`

   - Save and confirm it appears in the list.

2. **Navigate to the patient records**
   - In the patient row, click **Records**
   - You should land in a filtered records list for that patient.

3. **Create a clinical record**
   - Click **Add Record**
   - The patient should already be selected (or shown read-only depending on implementation).
   - Fill:
     - Symptoms
     - Diagnosis
     - Treatment

   - Save and verify it appears in the list.

4. **View details**
   - Click **View** on a patient → patient detail page should display recent records and navigation actions.
   - Click **View** on a record → record detail page should allow navigation back to patient and record lists.

5. **Edit / Delete**
   - Edit a patient (Document ID remains unique).
   - Edit a record (patient association should not be editable).
   - Delete a record.
   - Delete a patient (should remove related records if cascade is configured).

---

## Run locally (without Docker)

### 1) Start MySQL locally

Create database:

```sql
CREATE DATABASE clinical_db;
```

Default local connection used by the app (if you keep the defaults):

- URL: `jdbc:mysql://localhost:3306/clinical_db`
- User: `root`
- Password: `root`

> If your local MySQL uses a different user/password, override using environment variables (next step).

### 2) Configure environment variables (optional)

If your local MySQL differs, override with env vars:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/clinical_db"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="root"
```

### 3) Run Spring Boot

```bash
mvn clean package
mvn spring-boot:run
```

Then open:

- [http://localhost:8080/patients](http://localhost:8080/patients)

---

## Configuration strategy (single config for Docker + local)

The project uses a **single** `application.properties` with defaults for local usage:

- Local Spring Boot → connects to `localhost:3306`
- Docker Compose → overrides datasource via environment variables

Key properties:

- `spring.datasource.url=${SPRING_DATASOURCE_URL:...}`
- `spring.datasource.username=${SPRING_DATASOURCE_USERNAME:...}`
- `spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:...}`

This avoids profile-specific config files and keeps deployment consistent.

---

## Database & ORM

- ORM: Spring Data JPA + Hibernate
- DB: MySQL 8
- Schema strategy:
  - `spring.jpa.hibernate.ddl-auto=update`

This means:

- Tables are created/updated automatically from entity classes.
- Existing data is preserved when possible.
- Foreign keys and constraints are managed by Hibernate.

> Note: For production systems, migrations (Flyway/Liquibase) are preferred, but `ddl-auto=update` is acceptable for a learning/demo CRUD project.

---

## Project structure (high level)

- `src/main/java/com/example/clinic`
  - `ClinicApplication.java` — Spring Boot entry point
  - `controller/` — MVC controllers (routes and view models)
  - `service/` — business logic
  - `repository/` — Spring Data JPA repositories
  - `model/` — JPA entities

- `src/main/resources/templates/`
  - Thymeleaf templates (`patients/*`, `records/*`)

- `docker-compose.yml`
  - App + MySQL environment

- `Dockerfile`
  - Multi-stage build: Maven builder + minimal JRE runtime

---

## Smoke checks (quick verification)

### Check containers

```bash
docker compose ps
```

### Check app logs

```bash
docker compose logs -f app
```

### Check DB logs

```bash
docker compose logs -f db
```

### Verify DB is reachable

```bash
docker exec -it $(docker ps -qf "name=_db_") mysqladmin ping -uroot -proot
```

---

## Troubleshooting

### 1) Error: `mvn: command not found` during Docker build

Cause: using a base image without Maven in the build stage.

Fix: use a Maven image in the build stage (multi-stage Dockerfile):

- `maven:3.9.x-eclipse-temurin-17` for build
- `eclipse-temurin:17-jre` (or similar) for runtime

---

### 2) Error: `KeyError: 'ContainerConfig'` when running `docker-compose up`

Cause: legacy `docker-compose` (Python v1) sometimes breaks with newer Docker.

Fix: use Compose plugin:

```bash
docker compose up --build
```

If you must use legacy, clean old containers first:

```bash
docker-compose down
docker rm -f $(docker ps -aq) 2>/dev/null || true
```

But recommended: switch to `docker compose`.

---

### 3) Duplicate patient Document ID

The patient `documentId` is unique. If you try to create a patient with an existing value, the DB rejects it.

Expected behavior:

- UI should show a friendly message like:
  - “A patient with that Document ID already exists.”

---

### 4) Schema or timezone warnings in MySQL

If you see warnings related to timezone tables or server time zone, it usually won’t block the app.
If it blocks startup, set:

- MySQL server option `--default-time-zone=+00:00`
- Or add JDBC param `serverTimezone=UTC` to the datasource URL.

Example URL:

```text
jdbc:mysql://localhost:3306/clinical_db?serverTimezone=UTC
```
