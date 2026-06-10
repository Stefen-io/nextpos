# NextPOS

Desktop Point of Sale (POS) for mini-marts and convenience stores. Built with **Java 21**, **Maven**, **Swing** + **FlatLaf**, and **MySQL 8**.

## Prerequisites

| Requirement | Version |
|-------------|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| MySQL | 8+ |

Create the database before first run:

```sql
CREATE DATABASE nextpos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Quick Start

```bash
git clone <repository-url>
cd nextpos
```

1. Edit `src/main/resources/application.properties` — set `app.db.url`, `app.db.user`, and `app.db.password` for your MySQL instance (`nextpos` database).

2. **First-time setup** (drops all tables and re-seeds from SQL):

   ```bash
   mvn exec:java -Dapp.seed-on-startup=true
   ```

   > **Warning:** `app.seed-on-startup=true` runs `DROP TABLE` on every table, then loads `nextpos/sql/schema.sql`. Use only for initial setup or dev reset.

3. **Normal run** (no DB reset):

   ```bash
   mvn exec:java
   ```

## Build & Run

```bash
mvn clean package    # compile, test, shade fat JAR
mvn verify           # full lifecycle including tests
mvn package          # build fat JAR (skip tests: mvn package -DskipTests)
```

**Fat JAR output:** `target/nextpos-1.0-SNAPSHOT-all.jar`

```bash
java -jar target/nextpos-1.0-SNAPSHOT-all.jar
```

For webcam/barcode support, add the native-access flag:

```bash
java --enable-native-access=ALL-UNNAMED -jar target/nextpos-1.0-SNAPSHOT-all.jar
```

For `mvn exec:java`, the flag is applied via `.mvn/jvm.config` (same JVM as Maven). Override with `MAVEN_OPTS` if needed.

## Configuration

Settings live in `src/main/resources/application.properties`:

| Key | Default | Description |
|-----|---------|-------------|
| `app.data.dir` | `data` | Runtime data root (relative to working directory) |
| `app.db.url` | `jdbc:mysql://127.0.0.1:3306/nextpos` | JDBC connection URL |
| `app.db.user` | `root` | MySQL username |
| `app.db.password` | *(empty)* | MySQL password |
| `app.seed-on-startup` | `false` | Run SQL seed on startup (drops all tables) |

Override any key at runtime with `-D`:

```bash
mvn exec:java -Dapp.db.url=jdbc:mysql://127.0.0.1:3306/nextpos -Dapp.db.password=secret
mvn exec:java -Dapp.seed-on-startup=true
```

## Project Structure

```
nextpos/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/vn/edu/uit/nextpos/
│   │   │   ├── config/       # AppConfig, AppPaths
│   │   │   ├── dao/          # Database access
│   │   │   ├── models/       # Domain entities
│   │   │   ├── ui/           # Panels, dialogs, components
│   │   │   ├── util/         # DB, session, seeding, barcode
│   │   │   └── view/         # MainFrame, LoginFrame
│   │   └── resources/
│   │       ├── application.properties
│   │       └── nextpos/      # SQL seed, icons
│   └── test/java/            # Migration & unit tests
└── data/                     # Runtime data (created on first run)
    ├── pictures/
    └── session/
```

## Runtime Data

On startup the app creates `data/` under the working directory (configurable via `app.data.dir`).

| Path | Purpose |
|------|---------|
| `data/pictures/` | Product image uploads |
| `data/session/account.txt` | Saved credentials for auto-login on next launch |

## Upgrading from `pos_app` database

If you previously used the legacy `pos_app` MySQL schema, either seed a fresh `nextpos` database:

```bash
mvn exec:java -Dapp.seed-on-startup=true
```

Or copy data from the old database:

```bash
mysqldump -u <user> -p pos_app | mysql -u <user> -p nextpos
```

Then point `app.db.url` at `jdbc:mysql://127.0.0.1:3306/nextpos`.

## Testing

By default, `mvn test` runs unit tests (config, resources, util) without needing MySQL.

To run the DB smoke test, set `NEXTPOS_DB_TEST=true` and run:

```bash
NEXTPOS_DB_TEST=true mvn test -Dtest=DatabaseConnectionTest
```

For `DatabaseConnectionTest`, MySQL must be running with the database configured in `src/main/resources/application.properties` (`app.db.url`, `app.db.user`, `app.db.password`).

## Contributing

Contributions welcome. Open an issue or pull request with a clear description of the change.

## License

See repository license terms. *(License file TBD.)*
