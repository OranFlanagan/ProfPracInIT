# ProfPracInIT

Spring Boot support ticketing app with Thymeleaf UI, Supabase (PostgreSQL + Auth + Storage), and Resend for email.

## Requirements

- Java 21+
- Maven Wrapper (`./mvnw`)
- A [Supabase](https://supabase.com) project
- A [Resend](https://resend.com) account
- A verified domain (required for sending email via Resend)

---

## 1. Supabase Setup

### Database Tables

Run the following in the Supabase **SQL Editor** (Dashboard → SQL Editor → New query):

```sql
CREATE TABLE ticket (
    order_num BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    phone_num VARCHAR,
    invoice_num VARCHAR,
    school_organisation VARCHAR,
    issue_description TEXT,
    status VARCHAR DEFAULT 'UNASSIGNED',
    attachment_filename VARCHAR,
    supabase_filename VARCHAR,
    creation_time TIMESTAMP NOT NULL,
    assigned_staff VARCHAR,
    internal_notes TEXT
);

CREATE TABLE user_variable (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR,
    username VARCHAR,
    role VARCHAR,
    receive_notifications BOOLEAN DEFAULT FALSE
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR,
    category VARCHAR,
    url_string VARCHAR,
    code VARCHAR,
    featured_on_support_page BOOLEAN
);

CREATE TABLE issue (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR,
    product_id BIGINT REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE issue_fix (
    id BIGSERIAL PRIMARY KEY,
    fix_description VARCHAR,
    issue_id BIGINT REFERENCES issue(id) ON DELETE CASCADE
);

CREATE TABLE email_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE,
    content TEXT NOT NULL
);
```

### Storage Bucket

In **Storage**, create a bucket named `ticket_attachments`. Set it to **private**.

### Auth Redirect URLs

Invite links and password reset emails redirect back to the app after the user clicks them. Supabase will block these redirects unless your app URL is whitelisted:

1. Go to **Authentication → URL Configuration**
2. Set **Site URL** to your app URL (e.g. `http://localhost:8080`)
3. Under **Redirect URLs**, add the following:

   Local development:
   - `http://localhost:8080`
   - `http://localhost:8080/invite/accept`

   Live deployment:
   - `https://<your-domain>`
   - `https://<your-domain>/invite/accept`

---

## 2. Environment Variables

Create a `.env` file in the project root. All Supabase values are found under **Project Settings** in your Supabase dashboard.

```properties
# --- Database ---
# Project Settings → Database → Connection string → Transaction tab → URI
# Copy the URI, prefix with jdbc: and remove the credentials from the URL itself
# Example: jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:6543/postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://<pooler-host>:6543/postgres

# From the same Transaction URI — the username portion (e.g. postgres.yourprojectref)
SPRING_DATASOURCE_USERNAME=postgres.<project-ref>

# The password you set when creating the Supabase project
# Reset it any time at Project Settings → Database → Reset database password
SPRING_DATASOURCE_PASSWORD=<your-database-password>

# --- Supabase API ---
# Project Settings → API → Project URL
SUPABASE_URL=https://<project-ref>.supabase.co

# Project Settings → API → Project API Keys → anon / public
SUPABASE_KEY=<anon-public-key>

# Project Settings → API → Project API Keys → service_role (click reveal)
SUPABASE_SERVICE_ROLE_KEY=<service-role-key>

# Name of the storage bucket created in step 1
SUPABASE_BUCKET=ticket_attachments

# --- Resend ---
# resend.com → API Keys → Create API Key
RESEND_API_KEY=<your-resend-api-key>

# An email address on your Resend-verified domain
APP_MAIL_FROM=no-reply@yourdomain.com

# --- App ---
# Base URL used for invite and password reset redirect links
APP_URL=http://localhost:8080

# Optional — defaults to 50MB
MAX_FILE_SIZE=50MB
```

---

## 3. Resend Setup

1. Create an account at [resend.com](https://resend.com)
2. Go to **Domains** and add your domain (e.g. `yourdomain.com`)
3. Add the DNS records Resend provides to your domain registrar and wait for verification
4. Go to **API Keys → Create API Key** and copy the value into `RESEND_API_KEY`
5. Set `APP_MAIL_FROM` to an address on your verified domain (e.g. `no-reply@yourdomain.com`)

> If you don't have a domain yet, Resend allows you to send to your own email address
> using `onboarding@resend.dev` as the sender for testing purposes.

---

## 4. First User

The first admin account must be created manually as there is no public registration:

1. In your Supabase dashboard go to **Authentication → Users → Add User** and create the account
2. In the **Table Editor**, open `user_variable` and insert a row with:
   - `email` — matching the address you just created in Auth
   - `role` — `ROLE_ADMIN`
3. You can now log in and use **Admin → Invite User** to add all subsequent users

---

## 5. Run Locally

```bash
./mvnw spring-boot:run
```

Open `http://localhost:8080/login`
