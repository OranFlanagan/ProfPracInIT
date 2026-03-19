# ProfPracInIT

Spring Boot support ticket web app with Thymeleaf UI, H2 database, and email sending.

## Requirements

- Java 21
- Maven Wrapper (`./mvnw`)

## Environment Variables

The app reads mail credentials from environment variables (see `.env.example`):

- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `APP_MAIL_FROM` (optional, defaults to `MAIL_USERNAME`)

## Run Locally

1. Set environment variables in your shell:

```bash
export MAIL_USERNAME="your_mailtrap_username"
export MAIL_PASSWORD="your_mailtrap_password"
export APP_MAIL_FROM="no-reply@example.com"
```

2. Start the app:

```bash
./mvnw spring-boot:run
```

3. Open:

- `http://localhost:8080/navigation-page`
- `http://localhost:8080/h2-console`

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
