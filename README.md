# Spring Boot + Liquibase

A small demo of letting **Liquibase** own a MySQL schema in a Spring Boot
application: the app starts, the changelog runs, and the tables and seed data
exist. No Hibernate `ddl-auto`, no hand-applied SQL.

[![Java CI with Maven](https://github.com/hendisantika/springboot-liquibase/actions/workflows/maven.yml/badge.svg)](https://github.com/hendisantika/springboot-liquibase/actions/workflows/maven.yml)

## Stack

| | |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Liquibase | 5.0.3 (via `spring-boot-starter-liquibase`) |
| Database | MySQL 8.4 |
| Tests | JUnit 6 + Testcontainers 2.0 |

## Running it

You need a JDK 21 and a MySQL you can reach. The quickest MySQL:

```bash
docker run -d --name lb-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.4.0
```

Then start the app:

```bash
./mvnw spring-boot:run
```

It listens on <http://localhost:8080> and creates the `liquibase` database on
first connect (`createDatabaseIfNotExist=true`).

### Pointing at a different database

The defaults are `root`/`root` on `localhost:3306`. Override them with
environment variables rather than editing `application.properties`:

```bash
MYSQL_URL='jdbc:mysql://db.internal:3306/liquibase?useSSL=false' \
MYSQL_USER=app \
MYSQL_PASSWORD=secret \
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

The suite needs a running Docker daemon — it starts a throwaway MySQL 8.4
container via Testcontainers, wires it in with `@ServiceConnection`, and then
asserts that Liquibase actually did its job:

- every changeSet is recorded in `DATABASECHANGELOG`,
- the seed rows are present in `USERS`,
- no user row references a missing address.

No init script is used, because the schema is Liquibase's job — that is the
whole point of the project.

## The migrations

`spring.liquibase.change-log` points at
`src/main/resources/db/changelog/db.changelog-master.yaml`, which delegates to
three plain SQL files under `db/changelog/migration/`:

| changeSet | File | What it does |
|---|---|---|
| `createTable` | `01-create-users-and-addresses-schema.sql` | Creates `ADDRESSES` and `USERS`, with a FK from `USERS.ADDRESS` |
| `insertTableAddresses` | `02-insert-data-addresses.sql` | Seeds two addresses |
| `insertTableUsers` | `03-insert-data-users.sql` | Seeds two users |

Liquibase records each applied changeSet in `DATABASECHANGELOG`, so restarting
the app is a no-op:

```
Run:                          0
Previously run:               3
```

### Adding a migration

1. Drop a new `NN-what-it-does.sql` into `db/changelog/migration/`.
2. Append a `changeSet` to `db.changelog-master.yaml` referencing it with a
   fresh, never-reused `id`.

Do not edit a changeSet that has already run anywhere — Liquibase checksums
each one and will refuse to start when the contents change underneath it.

## Notes for anyone upgrading this project

Spring Boot 4 splits auto-configuration into per-technology modules. Depending
on `org.liquibase:liquibase-core` alone puts Liquibase on the classpath *without*
its auto-configuration, so the app starts happily and silently migrates nothing.
Use `spring-boot-starter-liquibase`.

Testcontainers 2.0 likewise renamed every module to a `testcontainers-*` prefix
(`junit-jupiter` → `testcontainers-junit-jupiter`, `mysql` → `testcontainers-mysql`).
