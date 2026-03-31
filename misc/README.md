# ProfPracInIT

Spring Boot support ticket web app with Thymeleaf UI, H2 database, and email sending.

## Requirements

- Java 21
- Maven Wrapper (`./mvnw`)

## Environment Variables

The app imports a root `.env` file if present. You can copy the values from `misc/.env.example` into a `.env` file in the project root.

Required variables for the current app configuration:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Optional mail variables:

- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `APP_MAIL_FROM` (optional, defaults to `MAIL_USERNAME`)

## Run Locally

1. Create a `.env` file in the project root.

Example local development values:

```properties
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
MAIL_USERNAME=your_mailtrap_username
MAIL_PASSWORD=your_mailtrap_password
APP_MAIL_FROM=no-reply@example.com
```

2. Start the app:

```bash
./mvnw spring-boot:run
```

3. Open:

- `http://localhost:8080/navigation-page`
- `http://localhost:8080/h2-console`

To use Supabase instead of H2, replace the three `SPRING_DATASOURCE_*` values in `.env` with the JDBC values from your Supabase project.

## Run In GitHub Codespaces

1. Start the app:

```bash
./mvnw spring-boot:run
```

2. Open forwarded port `8080` from the Ports panel.
3. Use the generated URL format:

- `https://<codespace-name>-8080.app.github.dev/navigation-page`

Note: Do not append `:8080` to the `app.github.dev` URL.

## Test

```bash
./mvnw clean test
```
